package com.watchparty.app.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.security.MessageDigest

class FileHasher(private val context: Context) {

    fun calculateSHA256(uri: Uri): Flow<HashProgress> = flow {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Cannot open file")

            val digest = MessageDigest.getInstance("SHA-256")
            val buffer = ByteArray(8192) // 8KB chunks
            var totalRead = 0L
            val fileSize = getFileSize(uri)

            inputStream.use { stream ->
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                    totalRead += bytesRead

                    if (fileSize > 0) {
                        val progress = totalRead.toFloat() / fileSize
                        emit(HashProgress.InProgress(progress))
                    }
                }
            }

            val hashBytes = digest.digest()
            val hash = hashBytes.joinToString("") { "%02x".format(it) }
            emit(HashProgress.Complete(hash))

        } catch (e: Exception) {
            emit(HashProgress.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    private fun getFileSize(uri: Uri): Long {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst() && sizeIndex != -1) {
                cursor.getLong(sizeIndex)
            } else {
                0L
            }
        } ?: 0L
    }
}

sealed class HashProgress {
    data class InProgress(val progress: Float) : HashProgress()
    data class Complete(val hash: String) : HashProgress()
    data class Error(val message: String) : HashProgress()
}
