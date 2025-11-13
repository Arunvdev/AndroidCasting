package com.example.androidcasting.core.network

import android.content.ContentResolver
import android.net.Uri
import android.os.ParcelFileDescriptor
import fi.iki.elonen.NanoHTTPD
import java.io.BufferedInputStream
import java.io.IOException
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Simple HTTP server that exposes locally stored media files via HTTP so that
 * DLNA renderers with limited codec support can stream the content directly.
 *
 * The server is intentionally lightweight. It can be extended to support range
 * requests, authentication, SSL offloading and more advanced caching policies.
 */
class LocalHttpServer(
    private val contentResolver: ContentResolver,
    private val port: Int = DEFAULT_PORT
) : NanoHTTPD(port) {

    private val isRunning = AtomicBoolean(false)

    @Throws(IOException::class)
    fun startServer() {
        if (isRunning.compareAndSet(false, true)) {
            try {
                start(SOCKET_READ_TIMEOUT, false)
            } catch (error: IOException) {
                isRunning.set(false)
                throw error
            }
        }
    }

    fun stopServer() {
        if (isRunning.compareAndSet(true, false)) {
            stop()
        }
    }

    override fun serve(session: IHTTPSession): Response {
        if (session.uri != "/stream") {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Unknown endpoint")
        }

        val uriParameter = session.parameters["uri"]?.firstOrNull()
            ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing uri parameter")

        val requestedUri = Uri.parse(uriParameter)
        val mimeType = session.parameters["mimeType"]?.firstOrNull()
            ?: contentResolver.getType(requestedUri)
            ?: "application/octet-stream"

        val parcel = contentResolver.openFileDescriptor(requestedUri, "r")
            ?: return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not accessible")

        val fileLength = parcel.statSize.takeIf { it >= 0 } ?: -1
        val inputStream = ParcelFileDescriptor.AutoCloseInputStream(parcel)
        val buffered = BufferedInputStream(inputStream)

        val rangeHeader = session.headers["range"]
        val response = if (rangeHeader != null && fileLength > 0) {
            val (start, end) = parseRange(rangeHeader, fileLength)
            if (start >= fileLength) {
                parcel.close()
                return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "Requested range not satisfiable")
            }

            buffered.skipFully(start)
            val contentLength = end - start + 1
            newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mimeType, buffered, contentLength).apply {
                addHeader("Content-Range", "bytes $start-$end/$fileLength")
                addHeader("Accept-Ranges", "bytes")
                addHeader("Content-Length", contentLength.toString())
            }
        } else {
            if (fileLength > 0) {
                newFixedLengthResponse(Response.Status.OK, mimeType, buffered, fileLength).apply {
                    addHeader("Accept-Ranges", "bytes")
                }
            } else {
                newChunkedResponse(Response.Status.OK, mimeType, buffered).apply {
                    addHeader("Accept-Ranges", "bytes")
                }
            }
        }

        response.addHeader("Cache-Control", "no-store")
        return response
    }

    fun resolveUrl(address: InetAddress, uri: Uri): String = buildString {
        append("http://")
        append(address.hostAddress)
        append(":")
        append(port)
        append("/stream?uri=")
        append(Uri.encode(uri.toString()))
        append("&mimeType=")
        append(Uri.encode(uriType(uri)))
    }

    private fun parseRange(range: String, fileLength: Long): Pair<Long, Long> {
        val spec = range.removePrefix("bytes=")
        val (startString, endString) = spec.split('-', limit = 2).let {
            it[0] to it.getOrNull(1)
        }

        val start = startString.toLongOrNull()?.coerceAtLeast(0) ?: 0L
        val end = when {
            endString.isNullOrEmpty() -> fileLength - 1
            else -> endString.toLongOrNull()?.coerceAtMost(fileLength - 1) ?: (fileLength - 1)
        }

        return start to if (end >= start) end else fileLength - 1
    }

    private fun BufferedInputStream.skipFully(bytes: Long) {
        var remaining = bytes
        while (remaining > 0) {
            val skipped = skip(remaining)
            if (skipped <= 0) {
                val read = read()
                if (read == -1) {
                    break
                }
                remaining--
            } else {
                remaining -= skipped
            }
        }
    }

    private fun uriType(uri: Uri): String = when (uri.toString().substringAfterLast('.', "").lowercase()) {
        "jpg", "jpeg" -> "image/jpeg"
        "png" -> "image/png"
        "gif" -> "image/gif"
        "mp4" -> "video/mp4"
        "mkv" -> "video/x-matroska"
        "avi" -> "video/x-msvideo"
        "mp3" -> "audio/mpeg"
        "aac" -> "audio/aac"
        "flac" -> "audio/flac"
        else -> contentResolver.getType(uri) ?: "application/octet-stream"
    }

    companion object {
        private const val DEFAULT_PORT = 8080
    }
}
