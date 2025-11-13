package com.example.androidcasting.data.repository

import android.content.Context
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
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Manages DLNA/UPnP discovery and playback commands. The repository exposes
 * discovered renderers as a flow. Casting is mocked and should be connected to
 * a real `AVTransport` implementation when upgrading the prototype.
 */
class DlnaCastingRepository(
    private val context: Context,
    private val httpServer: LocalHttpServer
) : CastingRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val targetsFlow = MutableStateFlow<List<CastingTarget>>(emptyList())
    private val upnpService: UpnpService = UpnpServiceImpl()

    private val registryListener = object : DefaultRegistryListener() {
        override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
            device?.let { updateDevices(registry) }
        }

        override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
            device?.let { updateDevices(registry) }
        }
    }

    init {
        upnpService.registry.addListener(registryListener)
        upnpService.controlPoint.search()
    }

    override fun availableRenderers(): Flow<List<CastingTarget>> = targetsFlow.asStateFlow()

    override suspend fun castTo(target: CastingTarget, mediaItem: MediaItem) {
        scope.launch {
            val wifiManager = context.getSystemService<WifiManager>() ?: return@launch
            val address = wifiManager.connectionInfo.ipAddress.toInetAddress()
            httpServer.startServer()
            val url = httpServer.resolveUrl(address, android.net.Uri.parse(mediaItem.uri))
            // TODO: send actual AVTransport control commands with the resolved URL.
        }
    }

    override suspend fun stopCasting() {
        scope.launch {
            httpServer.stopServer()
        }
    }

    private fun updateDevices(registry: Registry?) {
        scope.launch {
            val devices: List<CastingTarget> = registry?.devices?.mapNotNull { device ->
                device.takeIf { it.type.type == "MediaRenderer" }?.toCastingTarget()
            } ?: emptyList()
            targetsFlow.emit(devices)
        }
    }

    private fun Device<*, *, *>.toCastingTarget(): CastingTarget = CastingTarget(
        id = identity.udn.identifierString,
        friendlyName = details.friendlyName ?: "Unknown Renderer",
        protocols = services.map { it.serviceType.type },
        supportsCompatibilityMode = true
    )

    private fun Int.toInetAddress(): InetAddress {
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this)
        return InetAddress.getByAddress(buffer.array())
    }
}
