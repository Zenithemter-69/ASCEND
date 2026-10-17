package com.watchparty.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.watchparty.app.model.ConnectionStatus
import com.watchparty.app.model.WatchPartyUiState
import com.watchparty.app.network.*
import com.watchparty.app.sync.SyncEngine
import com.watchparty.app.util.FileHasher
import com.watchparty.app.util.HashProgress
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

class WatchPartyViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WatchPartyUiState())
    val uiState: StateFlow<WatchPartyUiState> = _uiState.asStateFlow()

    private var webSocketClient: WebSocketClient? = null
    private var syncEngine: SyncEngine? = null
    private var player: ExoPlayer? = null
    private val fileHasher = FileHasher(application)

    // TODO: Replace with actual server URL
    private val serverUrl = "ws://10.0.2.2:3000" // Android emulator localhost

    fun setPlayer(exoPlayer: ExoPlayer) {
        player = exoPlayer
        syncEngine = SyncEngine(exoPlayer) { position ->
            sendPositionUpdate(position)
        }
    }

    fun createRoom() {
        connectToServer()
        sendMessage(ClientMessage.CreateRoom())
    }

    fun joinRoom(roomCode: String) {
        connectToServer()
        sendMessage(ClientMessage.JoinRoom(roomCode = roomCode))
    }

    fun selectMovieFile(uri: Uri) {
        try {
            val takeFlags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            getApplication<Application>().contentResolver.takePersistableUriPermission(uri, takeFlags)
        } catch (e: Exception) {
            // Ignored
        }

        _uiState.update { it.copy(currentVideoUri = uri) }

        // Start file verification
        viewModelScope.launch {
            fileHasher.calculateSHA256(uri).collect { progress ->
                when (progress) {
                    is HashProgress.InProgress -> {
                        _uiState.update { it.copy(verificationProgress = progress.progress) }
                    }
                    is HashProgress.Complete -> {
                        _uiState.update {
                            it.copy(
                                verificationProgress = null,
                                isFileVerified = true
                            )
                        }
                        sendMessage(ClientMessage.FileHash(sha256 = progress.hash))

                        // Load video into player
                        player?.setMediaItem(MediaItem.fromUri(uri))
                        player?.prepare()
                    }
                    is HashProgress.Error -> {
                        _uiState.update {
                            it.copy(
                                verificationProgress = null,
                                errorMessage = "File verification failed: ${progress.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    fun toggleReady(isReady: Boolean) {
        if (isReady) {
            sendMessage(ClientMessage.Ready())
        } else {
            sendMessage(ClientMessage.NotReady())
        }
    }

    fun removeParticipant(participantId: String) {
        if (_uiState.value.isHost) {
            sendMessage(ClientMessage.RemoveParticipant(participantId = participantId))
        }
    }

    fun closeRoom() {
        if (_uiState.value.isHost) {
            sendMessage(ClientMessage.CloseRoom())
        }
    }

    fun transferHost(newHostId: String) {
        if (_uiState.value.isHost) {
            sendMessage(ClientMessage.TransferHost(newHostId = newHostId))
        }
    }

    fun seekToStart() {
        seek(0)
    }

    fun seekTo10s() {
        seek(10_000)
    }

    fun seekTo30s() {
        seek(30_000)
    }

    fun seek(position: Long) {
        if (_uiState.value.isHost) {
            sendMessage(ClientMessage.Seek(position = position))
        }
    }

    fun stopRoom() {
        _uiState.update { it.copy(errorMessage = "Room stopping is not yet implemented") }
    }

    fun play() {
        if (_uiState.value.isHost) {
            sendMessage(ClientMessage.Play())
        }
    }

    fun pause() {
        if (_uiState.value.isHost) {
            sendMessage(ClientMessage.Pause())
        }
    }

    fun leaveRoom() {
        webSocketClient?.disconnect()
        syncEngine?.stop()
        _uiState.value = WatchPartyUiState()
    }

    private fun connectToServer() {
        _uiState.update { it.copy(connectionStatus = ConnectionStatus.Connecting) }

        webSocketClient = WebSocketClient(serverUrl)

        viewModelScope.launch {
            webSocketClient?.connect()?.collect { event ->
                handleWebSocketEvent(event)
            }
        }
    }

    private fun handleWebSocketEvent(event: WebSocketEvent) {
        when (event) {
            is WebSocketEvent.Connected -> {
                _uiState.update { it.copy(connectionStatus = ConnectionStatus.Connected) }
                startClockSync()
            }
            is WebSocketEvent.Disconnected -> {
                _uiState.update { it.copy(connectionStatus = ConnectionStatus.Disconnected) }
            }
            is WebSocketEvent.MessageReceived -> {
                handleServerMessage(event.message)
            }
            is WebSocketEvent.Error -> {
                _uiState.update {
                    it.copy(
                        connectionStatus = ConnectionStatus.Disconnected,
                        errorMessage = event.error
                    )
                }
            }
        }
    }

    private fun handleServerMessage(messageJson: String) {
        try {
            val message = json.decodeFromString<ServerMessage>(messageJson)

            when (message.type) {
                "room_created" -> {
                    _uiState.update {
                        it.copy(
                            isInRoom = true,
                            isHost = true,
                            roomCode = message.roomCode ?: "",
                            participants = message.participants ?: emptyList()
                        )
                    }
                }
                "room_joined" -> {
                    _uiState.update {
                        it.copy(
                            isInRoom = true,
                            isHost = message.isHost ?: false,
                            roomCode = message.roomCode ?: "",
                            participants = message.participants ?: emptyList()
                        )
                    }
                }
                "participant_list" -> {
                    _uiState.update {
                        it.copy(participants = message.participants ?: emptyList())
                    }
                }
                "room_closed" -> {
                    leaveRoom()
                    _uiState.update { it.copy(errorMessage = "Room was closed") }
                }
                "removed_from_room" -> {
                    leaveRoom()
                    _uiState.update { it.copy(errorMessage = "You were removed from the room") }
                }
                "host_transferred" -> {
                    if (message.isHost == true) {
                        _uiState.update {
                            it.copy(
                                isHost = true,
                                errorMessage = "You are now the host"
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isHost = false,
                                errorMessage = "You are no longer the host"
                            )
                        }
                    }
                }
                "participant_buffering" -> {
                    message.participantId?.let { participantId ->
                        _uiState.update {
                            it.copy(errorMessage = "Participant $participantId is buffering, playback may be affected")
                        }
                    }
                }
                "file_hash_mismatch" -> {
                    _uiState.update {
                        it.copy(errorMessage = "File hash does not match. Ensure all participants have the same file.")
                    }
                }
                "play_scheduled" -> {
                    message.playAt?.let { playAt ->
                        message.position?.let { position ->
                            syncEngine?.schedulePlay(playAt, position)
                        }
                    }
                }
                "pause" -> {
                    syncEngine?.pause()
                }
                "seek" -> {
                    message.position?.let { position ->
                        syncEngine?.seek(position)
                    }
                }
                "sync_pong" -> {
                    message.serverTime?.let { serverTime ->
                        message.clientTime?.let { clientTime ->
                            val receiveTime = System.currentTimeMillis()
                            syncEngine?.updateServerTimeOffset(clientTime, serverTime, receiveTime)
                        }
                    }
                }
                "error" -> {
                    _uiState.update { it.copy(errorMessage = message.error) }
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Failed to parse server message") }
        }
    }

    private fun startClockSync() {
        viewModelScope.launch {
            // Perform multiple sync pings for better accuracy
            repeat(3) {
                val clientTime = System.currentTimeMillis()
                sendMessage(ClientMessage.SyncPing(clientTime = clientTime))
                kotlinx.coroutines.delay(100)
            }
        }
    }

    private fun sendPositionUpdate(position: Long) {
        sendMessage(ClientMessage.PositionUpdate(position = position))
    }

    fun sendBuffering(isBuffering: Boolean) {
        sendMessage(ClientMessage.Buffering(isBuffering = isBuffering))
    }

    private fun sendMessage(message: ClientMessage) {
        try {
            val jsonString = json.encodeToString(message)
            webSocketClient?.send(jsonString)
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Failed to send message") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketClient?.disconnect()
        syncEngine?.stop()
    }
}
