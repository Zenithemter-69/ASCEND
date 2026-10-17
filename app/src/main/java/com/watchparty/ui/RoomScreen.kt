package com.watchparty.app.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.watchparty.app.model.WatchPartyUiState
import com.watchparty.app.network.Participant
import com.watchparty.app.viewmodel.WatchPartyViewModel

@Composable
fun RoomScreen(
    viewModel: WatchPartyViewModel,
    uiState: WatchPartyUiState
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    var isReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.setPlayer(exoPlayer)
        onDispose {
            exoPlayer.release()
        }
    }

    // Add listener for buffering state changes
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        viewModel.sendBuffering(true)
                    }
                    Player.STATE_READY, Player.STATE_ENDED -> {
                        viewModel.sendBuffering(false)
                    }
                    else -> {}
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Video player
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top info bar
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Room Code: ${uiState.roomCode}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (uiState.isHost) "You are Host" else "Participant",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (uiState.currentVideoUri == null) {
                        FilePickerButton(onFilePicked = { uri -> viewModel.selectMovieFile(uri) })
                    } else if (uiState.isFileVerified) {
                        Button(
                            onClick = {
                                isReady = !isReady
                                viewModel.toggleReady(isReady)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReady) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(if (isReady) "Ready ✓" else "Mark Ready")
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (uiState.isHost) {
                                viewModel.closeRoom()
                            } else {
                                viewModel.leaveRoom()
                            }
                        }
                    ) {
                        Text(if (uiState.isHost) "Close Room" else "Leave")
                    }
                }
            }
        }

        // Verification progress
        val verificationProgress = uiState.verificationProgress
        if (verificationProgress != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Verifying Local Movie File...")
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = verificationProgress,
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${(verificationProgress * 100).toInt()}%")
                }
            }
        } else if (uiState.currentVideoUri != null && !uiState.isFileVerified) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Select a movie file to verify",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Participant list overlay (Left side)
        Surface(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
                .width(240.dp)
                .heightIn(max = 280.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Participants (${uiState.participants.size})",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(uiState.participants) { participant ->
                        ParticipantItem(
                            participant = participant,
                            canRemove = uiState.isHost && !participant.isHost,
                            onRemove = { viewModel.removeParticipant(participant.id) }
                        )
                    }
                }
            }
        }

        // Host controls overlay at bottom
        HostControlsOverlay(
            isHost = uiState.isHost,
            isFileVerified = uiState.isFileVerified,
            uiState = uiState,
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )

        // Error message popup
        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(error)
            }
        }
    }
}

@Composable
fun ParticipantItem(
    participant: Participant,
    canRemove: Boolean,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = (if (participant.isHost) "👑 " else "👤 ") + participant.id.take(6),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = when {
                    !participant.isVerified -> "Hashing..."
                    participant.isReady -> "Ready ✓"
                    else -> "Not ready"
                },
                style = MaterialTheme.typography.labelSmall,
                color = when {
                    participant.isReady -> MaterialTheme.colorScheme.primary
                    !participant.isVerified -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }
            )

            if (participant.isBuffering) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("(Buffering...)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        if (canRemove) {
            TextButton(
                onClick = onRemove,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text("Kick", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun FilePickerButton(onFilePicked: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { onFilePicked(it) }
    }

    Button(onClick = { launcher.launch(arrayOf("video/*")) }) {
        Text("Select Movie")
    }
}

@Composable
fun HostControlsOverlay(
    isHost: Boolean,
    isFileVerified: Boolean,
    uiState: WatchPartyUiState,
    viewModel: WatchPartyViewModel,
    modifier: Modifier = Modifier
) {
    val allReady = uiState.participants.isNotEmpty() && uiState.participants.all {
        it.isVerified && it.isReady
    }

    if (isHost && isFileVerified) {
        // Alignment and padding are already applied by the caller via `modifier`
        Surface(
            modifier = modifier,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!allReady) {
                    Text(
                        text = "Waiting for all participants to verify and be ready...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { viewModel.play() },
                        enabled = allReady
                    ) {
                        Text("Play")
                    }
                    Button(onClick = { viewModel.pause() }) {
                        Text("Pause")
                    }
                }
            }
        }
    }
}

@Composable
fun SeekControls(
    isHost: Boolean,
    onSeekToStart: () -> Unit,
    onSeekTo10s: () -> Unit,
    onSeekTo30s: () -> Unit,
    onTransferHost: (String) -> Unit,
    onStopRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isHost) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onSeekToStart() }) {
            Text("Start")
        }
        Button(onClick = { onSeekTo10s() }) {  // 10 seconds
            Text("10s")
        }
        Button(onClick = { onSeekTo30s() }) {  // 30 seconds
            Text("30s")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { /* Add transfer logic here when ready */ }) {
            Text("Transfer Host")
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = { onStopRoom() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
            Text("Stop")
        }
    }
}
