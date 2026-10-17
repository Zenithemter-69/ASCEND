package com.watchparty.app.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val json = Json { ignoreUnknownKeys = true }

// Client -> Server messages
@Serializable
sealed class ClientMessage {
    @Serializable
    data class CreateRoom(val type: String = "create_room") : ClientMessage()

    @Serializable
    data class JoinRoom(
        val type: String = "join_room",
        val roomCode: String
    ) : ClientMessage()

    @Serializable
    data class FileHash(
        val type: String = "file_hash",
        val sha256: String
    ) : ClientMessage()

    @Serializable
    data class Ready(val type: String = "ready") : ClientMessage()

    @Serializable
    data class NotReady(val type: String = "not_ready") : ClientMessage()

    @Serializable
    data class RemoveParticipant(
        val type: String = "remove_participant",
        val participantId: String
    ) : ClientMessage()

    @Serializable
    data class CloseRoom(val type: String = "close_room") : ClientMessage()

    @Serializable
    data class Play(val type: String = "play") : ClientMessage()

    @Serializable
    data class Pause(val type: String = "pause") : ClientMessage()

    @Serializable
    data class Seek(
        val type: String = "seek",
        val position: Long
    ) : ClientMessage()

    @Serializable
    data class SyncPing(
        val type: String = "sync_ping",
        val clientTime: Long
    ) : ClientMessage()

    @Serializable
    data class PositionUpdate(
        val type: String = "position_update",
        val position: Long
    ) : ClientMessage()

    @Serializable
    data class Buffering(
        val type: String = "buffering",
        val isBuffering: Boolean
    ) : ClientMessage()

    @Serializable
    data class TransferHost(
        val type: String = "transfer_host",
        val newHostId: String
    ) : ClientMessage()
}

@Serializable
data class Participant(
    val id: String,
    val isHost: Boolean,
    val isVerified: Boolean,
    val isReady: Boolean,
    val isBuffering: Boolean = false
)

// Server -> Client messages
@Serializable
data class ServerMessage(
    val type: String,
    val roomCode: String? = null,
    val isHost: Boolean? = null,
    val sha256: String? = null,
    val participants: List<Participant>? = null,
    val playAt: Long? = null,
    val position: Long? = null,
    val serverTime: Long? = null,
    val clientTime: Long? = null,
    val error: String? = null,
    val participantId: String? = null
)