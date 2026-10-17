package com.watchparty.app.model

import android.net.Uri
import com.watchparty.app.network.Participant

data class WatchPartyUiState(
    val isInRoom: Boolean = false,
    val isHost: Boolean = false,
    val roomCode: String = "",
    val currentVideoUri: Uri? = null,
    val isFileVerified: Boolean = false,
    val verificationProgress: Float? = null,
    val isPlaying: Boolean = false,
    val participants: List<Participant> = emptyList(),
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val errorMessage: String? = null
)

enum class ConnectionStatus {
    Disconnected,
    Connecting,
    Connected,
    Reconnecting
}
