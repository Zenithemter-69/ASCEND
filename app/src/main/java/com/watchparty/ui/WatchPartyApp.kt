package com.watchparty.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.watchparty.app.viewmodel.WatchPartyViewModel

@Composable
fun WatchPartyApp(
    viewModel: WatchPartyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isInRoom -> {
                RoomScreen(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
            else -> {
                LobbyScreen(
                    onCreateRoom = { viewModel.createRoom() },
                    onJoinRoom = { code -> viewModel.joinRoom(code) }
                )
            }
        }
    }
}

@Composable
fun LobbyScreen(
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit
) {
    var roomCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "WatchParty",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onCreateRoom,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Create Room")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("OR", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = roomCode,
            onValueChange = { roomCode = it.uppercase() },
            label = { Text("Room Code") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onJoinRoom(roomCode) },
            enabled = roomCode.length == 6,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Join Room")
        }
    }
}
