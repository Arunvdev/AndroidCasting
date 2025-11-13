package com.example.androidcasting.core.network

import android.net.Uri
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
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
class LocalHttpServer(private val port: Int = DEFAULT_PORT) : NanoHTTPD(port) {

    private val isRunning = AtomicBoolean(false)

    @Throws(IOException::class)
    fun startServer() {
        if (isRunning.compareAndSet(false, true)) {
            start(SOCKET_READ_TIMEOUT, false)
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

        val uri = session.parameters["uri"]?.firstOrNull()
            ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing uri parameter")

        val file = File(Uri.parse(uri).path ?: return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid uri"))
        if (!file.exists()) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not found")
        }

        val mimeType = session.parameters["mimeType"]?.firstOrNull() ?: "application/octet-stream"
        val inputStream = FileInputStream(file)
        return newChunkedResponse(Response.Status.OK, mimeType, inputStream)
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
        else -> "application/octet-stream"
    }

    companion object {
        private const val DEFAULT_PORT = 8080
    }
}
