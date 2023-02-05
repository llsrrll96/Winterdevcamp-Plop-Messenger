package com.plop.plopmessenger.domain.repository

import com.plop.plopmessenger.data.local.dao.ChatRoomMemberImage
import com.plop.plopmessenger.data.local.entity.ChatRoom
import kotlinx.coroutines.flow.Flow
import java.util.*

interface ChatRoomRepository {
    fun loadChatRoomTitle(chatroomId: String): Flow<String>
    fun loadChatRoomAndMessage(): Flow<List<ChatRoomMemberImage>>
    fun loadChatRoomAndMemberById(chatroomId: String): Flow<ChatRoomMemberImage>
    fun hasPersonalChatRoomByFriend(friendId: String): Flow<String?>
    suspend fun insertChatRoom(chatRoom: ChatRoom)
    suspend fun insertAllChatRoom(chatRooms: List<ChatRoom>)
    suspend fun updateChatRoom(chatroom: ChatRoom)
    suspend fun updateChatroomAll(chatrooms: List<ChatRoom>)
    suspend fun updateChatRoomTitleById(chatroomId: String, title: String)
    suspend fun updateChatRoomUnreadById(chatroomId: String, unread: Int)
    suspend fun plusChatRoomUnreadById(chatroomId: String, unread: Int)
    suspend fun updateChatRoomContentById(chatroomId: String, content: String, updatedAt: Date)
    suspend fun deleteChatRoom(chatroomId: String)
}