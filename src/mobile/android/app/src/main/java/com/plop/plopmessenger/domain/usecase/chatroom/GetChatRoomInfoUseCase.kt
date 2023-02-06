package com.plop.plopmessenger.domain.usecase.chatroom

import android.util.Log
import com.plop.plopmessenger.domain.model.ChatRoom
import com.plop.plopmessenger.domain.model.toChatRoom
import com.plop.plopmessenger.domain.repository.ChatRoomRepository
import com.plop.plopmessenger.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetChatRoomInfoUseCase @Inject constructor(
    private val chatRoomRepository: ChatRoomRepository
) {
    operator fun invoke(chatRoomId: String): Flow<Resource<ChatRoom>> = flow {
        try {
            emit(Resource.Loading())
            chatRoomRepository.loadChatRoomAndMemberById(chatRoomId).collect(){ result ->
                emit(Resource.Success(result.toChatRoom()))
            }
        } catch (e: IOException) {
            Log.d("GetChatRoomInfoUseCase", "IOException")
        } catch (e: Exception) {
            Log.d("GetChatRoomInfoUseCase", e.message.toString())
        }
    }
}