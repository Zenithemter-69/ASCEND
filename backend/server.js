const express = require('express');
const http = require('http');
const WebSocket = require('ws');
const crypto = require('crypto');

const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

// Room storage
const rooms = new Map();

class Room {
    constructor(code) {
        this.code = code;
        this.host = null;
        this.participants = new Map();
        this.fileHash = null;
        this.currentState = {
            isPlaying: false,
            position: 0
        };
    }

    addParticipant(ws, isHost = false) {
        const id = crypto.randomUUID();
        this.participants.set(id, {
            id,
            ws,
            isHost,
            isVerified: false,
            isReady: false
        });

        if (isHost) {
            this.host = id;
        }

        return id;
    }

    removeParticipant(id) {
        this.participants.delete(id);

        // If host leaves, close the room
        if (id === this.host) {
            this.broadcast({ type: 'room_closed', reason: 'Host left' });
            return true; // Signal to delete room
        }

        this.broadcastParticipantList();
        return false;
    }

    getParticipantList() {
        const list = [];
        this.participants.forEach((p) => {
            list.push({
                id: p.id,
                isHost: p.isHost,
                isVerified: p.isVerified,
                isReady: p.isReady,
                isBuffering: p.isBuffering || false
            });
        });
        return list;
    }

    broadcastParticipantList() {
        this.broadcast({
            type: 'participant_list',
            participants: this.getParticipantList()
        });
    }

    areAllParticipantsReady() {
        let allReady = true;
        this.participants.forEach((p) => {
            if (!p.isVerified || !p.isReady) {
                allReady = false;
            }
        });
        return allReady;
    }

    broadcast(message, excludeId = null) {
        this.participants.forEach((participant, id) => {
            if (id !== excludeId && participant.ws.readyState === WebSocket.OPEN) {
                participant.ws.send(JSON.stringify(message));
            }
        });
    }

    send(id, message) {
        const participant = this.participants.get(id);
        if (participant && participant.ws.readyState === WebSocket.OPEN) {
            participant.ws.send(JSON.stringify(message));
        }
    }
}

function generateRoomCode() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code;
    do {
        code = Array.from({ length: 6 }, () =>
            chars[Math.floor(Math.random() * chars.length)]
        ).join('');
    } while (rooms.has(code));
    return code;
}

wss.on('connection', (ws) => {
    let currentRoomCode = null;
    let participantId = null;

    ws.on('message', (data) => {
        try {
            const message = JSON.parse(data.toString());

            switch (message.type) {
                case 'create_room': {
                    const code = generateRoomCode();
                    const room = new Room(code);
                    participantId = room.addParticipant(ws, true);
                    currentRoomCode = code;
                    rooms.set(code, room);

                    ws.send(JSON.stringify({
                        type: 'room_created',
                        roomCode: code,
                        isHost: true,
                        participants: room.getParticipantList()
                    }));
                    break;
                }

                case 'join_room': {
                    const room = rooms.get(message.roomCode);
                    if (!room) {
                        ws.send(JSON.stringify({
                            type: 'error',
                            error: 'Room not found'
                        }));
                        break;
                    }

                    participantId = room.addParticipant(ws, false);
                    currentRoomCode = message.roomCode;

                    ws.send(JSON.stringify({
                        type: 'room_joined',
                        roomCode: message.roomCode,
                        isHost: false,
                        sha256: room.fileHash,
                        participants: room.getParticipantList()
                    }));

                    room.broadcastParticipantList();
                    break;
                }

                case 'file_hash': {
                    const room = rooms.get(currentRoomCode);
                    if (!room) break;

                    const participant = room.participants.get(participantId);
                    if (!participant) break;

                    if (room.fileHash === null) {
                        room.fileHash = message.sha256;
                        participant.isVerified = true;
                        ws.send(JSON.stringify({ type: 'file_hash_verified' }));
                    } else if (room.fileHash !== message.sha256) {
                        participant.isVerified = false;
                        ws.send(JSON.stringify({
                            type: 'file_hash_mismatch',
                            error: 'Your file does not match the room reference file'
                        }));
                    } else {
                        participant.isVerified = true;
                        ws.send(JSON.stringify({ type: 'file_hash_verified' }));
                    }

                    room.broadcastParticipantList();
                    break;
                }

                case 'ready': {
                    const room = rooms.get(currentRoomCode);
                    if (!room) break;

                    const participant = room.participants.get(participantId);
                    if (!participant) break;

                    if (!participant.isVerified) {
                        ws.send(JSON.stringify({
                            type: 'error',
                            error: 'Cannot become ready before successful file verification'
                        }));
                        break;
                    }

                    participant.isReady = true;
                    room.broadcastParticipantList();
                    break;
                }

                case 'not_ready': {
                    const room = rooms.get(currentRoomCode);
                    if (!room) break;

                    const participant = room.participants.get(participantId);
                    if (!participant) break;

                    participant.isReady = false;
                    room.broadcastParticipantList();
                    break;
                }

                case 'play': {
                    const room = rooms.get(currentRoomCode);
                    if (!room || participantId !== room.host) {
                        ws.send(JSON.stringify({
                            type: 'error',
                            error: 'Only host can control playback'
                        }));
                        break;
                    }

                    if (!room.areAllParticipantsReady()) {
                        ws.send(JSON.stringify({
                            type: 'error',
                            error: 'Cannot start playback until all participants are verified and ready'
                        }));
                        break;
                    }

                    // Schedule playback 200ms in the future
                    const playAt = Date.now() + 200;
                    const position = room.currentState.position;

                    room.currentState.isPlaying = true;
                    room.broadcast({
                        type: 'play_scheduled',
                        playAt,
                        position
                    });
                    break;
                }

                case 'pause': {
                    const room = rooms.get(currentRoomCode);
                    if (!room || participantId !== room.host) {
                        ws.send(JSON.stringify({
                            type: 'error',
                            error: 'Only host can control playback'
                        }));
                        break;
                    }

                    room.currentState.isPlaying = false;
                    room.broadcast({ type: 'pause' });
                    break;
                }

                case 'seek': {
                    const room = rooms.get(currentRoomCode);
                    if (!room || participantId !== room.host) {
                        ws.send(JSON.stringify({
                            type: 'error',
                            error: 'Only host can control playback'
                        }));
                        break;
                    }

                    room.currentState.position = message.position;
                    room.broadcast({
                        type: 'seek',
                        position: message.position
                    });
                    break;
                }

                case 'sync_ping': {
                    ws.send(JSON.stringify({
                        type: 'sync_pong',
                        serverTime: Date.now(),
                        clientTime: message.clientTime
                    }));
                    break;
                }

                case 'position_update': {
                    const room = rooms.get(currentRoomCode);
                    if (room && participantId === room.host) {
                        room.currentState.position = message.position;
                    }
                    break;
                }

                case 'buffering': {
                    const room = rooms.get(currentRoomCode);
                    if (!room) break;

                    const participant = room.participants.get(participantId);
                    if (!participant) break;

                    participant.isBuffering = message.isBuffering;
                    room.broadcastParticipantList();

                    // If buffering policy is enabled, inform host
                    if (message.isBuffering) {
                        room.broadcast({
                            type: 'participant_buffering',
                            participantId: participantId
                        }, participantId === room.host ? null : room.host);
                    }
                    break;
                }

                case 'remove_participant': {
                    const room = rooms.get(currentRoomCode);
                    if (!room || participantId !== room.host) break;

                    const targetId = message.participantId;
                    const targetParticipant = room.participants.get(targetId);
                    if (targetParticipant) {
                        targetParticipant.ws.send(JSON.stringify({
                            type: 'removed_from_room',
                            reason: 'Removed by host'
                        }));
                        targetParticipant.ws.close();
                        room.removeParticipant(targetId);
                    }
                    break;
                }

                case 'transfer_host': {
                    const room = rooms.get(currentRoomCode);
                    if (!room || participantId !== room.host) break;

                    const newHostId = message.newHostId;
                    const newHost = room.participants.get(newHostId);
                    if (!newHost) break;

                    room.host = newHostId;
                    newHost.isHost = true;

                    // Remove host flag from old host
                    const oldHost = room.participants.get(room.host);
                    if (oldHost && oldHost.id !== room.host) {
                        oldHost.isHost = false;
                    }

                    // Notify new host and old host
                    newHost.ws.send(JSON.stringify({
                        type: 'host_transferred',
                        newHost: true
                    }));

                    if (oldHost && oldHost.id !== newHostId && oldHost.ws.readyState === WebSocket.OPEN) {
                        oldHost.ws.send(JSON.stringify({
                            type: 'host_transferred',
                            newHost: false,
                            newHostId: newHostId
                        }));
                    }

                    room.broadcastParticipantList();
                    break;
                }

                case 'close_room': {
                    const room = rooms.get(currentRoomCode);
                    if (!room || participantId !== room.host) break;

                    room.broadcast({ type: 'room_closed', reason: 'Room closed by host' });
                    rooms.delete(currentRoomCode);
                    break;
                }
            }
        } catch (error) {
            console.error('Error handling message:', error);
            ws.send(JSON.stringify({
                type: 'error',
                error: 'Invalid message format'
            }));
        }
    });

    ws.on('close', () => {
        if (currentRoomCode && participantId) {
            const room = rooms.get(currentRoomCode);
            if (room) {
                const shouldDelete = room.removeParticipant(participantId);
                if (shouldDelete) {
                    rooms.delete(currentRoomCode);
                }
            }
        }
    });

    ws.on('error', (error) => {
        console.error('WebSocket error:', error);
    });
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`WatchParty server running on port ${PORT}`);
});
