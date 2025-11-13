package com.example.androidcasting.data.repository

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import androidx.core.content.getSystemService
import com.example.androidcasting.core.network.LocalHttpServer
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CastingRepository
import com.example.androidcasting.domain.repository.CastingTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.fourthline.cling.UpnpService
import org.fourthline.cling.UpnpServiceImpl
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.support.avtransport.callback.Play
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI
import org.fourthline.cling.support.avtransport.callback.Stop
import org.fourthline.cling.support.model.DIDLContent
import org.fourthline.cling.support.model.Res
import org.fourthline.cling.support.model.item.AudioItem
import org.fourthline.cling.support.model.item.ImageItem
import org.fourthline.cling.support.model.item.MovieItem
import org.fourthline.cling.support.model.item.VideoItem
import org.fourthline.cling.support.model.PersonWithRole
import org.fourthline.cling.support.model.ProtocolInfo
import org.fourthline.cling.support.model.WriteStatus
import org.fourthline.cling.support.model.container.Container
import org.fourthline.cling.support.model.container.StorageFolder
import org.fourthline.cling.support.model.DIDLParser
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Manages DLNA/UPnP discovery and playback commands. The repository exposes
 * discovered renderers as a flow and issues AVTransport commands for real
 * devices, falling back to the built-in HTTP server when compatibility mode is
 * required.
 */
class DlnaCastingRepository(
    private val context: Context,
    private val httpServer: LocalHttpServer
) : CastingRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val targetsFlow = MutableStateFlow<List<CastingTarget>>(emptyList())
    private val upnpService: UpnpService = UpnpServiceImpl()
    private val deviceCache = ConcurrentHashMap<String, RemoteDevice>()
    private val currentTarget = AtomicReference<String?>(null)
    private val multicastLock = context.getSystemService<WifiManager>()
        ?.createMulticastLock("androidcasting:mdns")?.apply { setReferenceCounted(true); acquire() }

    private val registryListener = object : DefaultRegistryListener() {
        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
            device?.let { updateDevices(registry) }
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
            device?.let { remote ->
                deviceCache.remove(remote.identity.udn.identifierString)
                updateDevices(registry)
            }
        }
    }

    init {
        upnpService.registry.addListener(registryListener)
        upnpService.controlPoint.search()
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                multicastLock?.release()
            } catch (_: Exception) {
            }
            try {
                upnpService.shutdown()
            } catch (_: Exception) {
            }
        })
    }

    override fun availableRenderers(): Flow<List<CastingTarget>> = targetsFlow.asStateFlow()

    override suspend fun castTo(target: CastingTarget, mediaItem: MediaItem) {
        scope.launch {
            val device = deviceCache[target.id] ?: return@launch
            val wifiManager = context.getSystemService<WifiManager>() ?: return@launch
            val ip = wifiManager.connectionInfo.ipAddress
            val address = ip.toInetAddress() ?: return@launch

            try {
                httpServer.startServer()
            } catch (error: Exception) {
                return@launch
            }

            val streamUrl = httpServer.resolveUrl(address, Uri.parse(mediaItem.uri))
            val avTransport = device.findService(UDAServiceType("AVTransport")) ?: return@launch
            val metadata = buildDidlMetadata(mediaItem, streamUrl)

            upnpService.controlPoint.execute(object : SetAVTransportURI(avTransport, "0", streamUrl, metadata) {
                override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                    currentTarget.set(target.id)
                    upnpService.controlPoint.execute(object : Play(avTransport) {
                        override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                            // Successful play command
                        }

                        override fun failure(
                            invocation: ActionInvocation<out Service<*, *>>?,
                            operation: UpnpResponse?,
                            defaultMsg: String?
                        ) {
                            // noop - renderer feedback will be surfaced via UI state observers
                        }
                    })
                }

                override fun failure(
                    invocation: ActionInvocation<out Service<*, *>>?,
                    operation: UpnpResponse?,
                    defaultMsg: String?
                ) {
                    // Failed to set URI, stop the local server to avoid leaking file handles
                    httpServer.stopServer()
                }
            })
        }
    }

    override suspend fun stopCasting() {
        scope.launch {
            val targetId = currentTarget.get()
            if (targetId != null) {
                val device = deviceCache[targetId]
                val avTransport = device?.findService(UDAServiceType("AVTransport"))
                if (avTransport != null) {
                    upnpService.controlPoint.execute(object : Stop(avTransport) {
                        override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                            currentTarget.set(null)
                        }

                        override fun failure(
                            invocation: ActionInvocation<out Service<*, *>>?,
                            operation: UpnpResponse?,
                            defaultMsg: String?
                        ) {
                            currentTarget.set(null)
                        }
                    })
                } else {
                    currentTarget.set(null)
                }
            }
            httpServer.stopServer()
        }
    }

    private fun updateDevices(registry: Registry?) {
        scope.launch {
            val devices: List<CastingTarget> = registry?.devices?.mapNotNull { device ->
                device.takeIf { it.type.type == "MediaRenderer" }?.toCastingTarget()
            } ?: emptyList()
            val ids = devices.map { it.id }.toSet()
            deviceCache.keys.removeIf { it !in ids }
            targetsFlow.emit(devices)
        }
    }

    private fun Device<*, *, *>.toCastingTarget(): CastingTarget = CastingTarget(
        id = identity.udn.identifierString,
        friendlyName = details.friendlyName ?: "Unknown Renderer",
        protocols = services.map { it.serviceType.type },
        supportsCompatibilityMode = true
    ).also {
        if (this is RemoteDevice) {
            deviceCache[identity.udn.identifierString] = this
        }
    }

    private fun Int.toInetAddress(): InetAddress? {
        if (this == 0) return null
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this)
        return InetAddress.getByAddress(buffer.array())
    }

    private fun buildDidlMetadata(mediaItem: MediaItem, url: String): String {
        val didl = DIDLContent()
        val container: Container = StorageFolder().apply {
            id = "0"
            parentID = "-1"
            title = "Android Casting"
            childCount = 1
            writeStatus = WriteStatus.NOT_WRITABLE
        }
        didl.addContainer(container)

        val res = Res(ProtocolInfo("http-get:*:${mediaItem.mimeType}:*"), url).apply {
            size = mediaItem.sizeBytes.takeIf { it > 0 }
            duration = mediaItem.durationMillis.takeIf { it > 0 }?.let { millis ->
                val totalSeconds = millis / 1000
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            }
            bitrate = mediaItem.codecInfo?.bitrate?.toInt()
            resolution = mediaItem.codecInfo?.let { info ->
                val width = info.width ?: return@let null
                val height = info.height ?: return@let null
                "${width}x$height"
            }
        }

        val item = when (mediaItem.type) {
            com.example.androidcasting.domain.model.MediaType.PHOTO -> ImageItem(
                mediaItem.id,
                container.id,
                mediaItem.title,
                "Android Casting",
                res
            )
            com.example.androidcasting.domain.model.MediaType.AUDIO -> AudioItem(
                mediaItem.id,
                container.id,
                mediaItem.title,
                PersonWithRole("Android Casting", "Composer"),
                res
            )
            com.example.androidcasting.domain.model.MediaType.VIDEO -> MovieItem(
                mediaItem.id,
                container.id,
                mediaItem.title,
                "Android Casting",
                res
            )
            com.example.androidcasting.domain.model.MediaType.FILE -> VideoItem(
                mediaItem.id,
                container.id,
                mediaItem.title,
                "Android Casting",
                res
            )
        }

        didl.addItem(item)
        return DIDLParser().generate(didl, true)
    }
}
