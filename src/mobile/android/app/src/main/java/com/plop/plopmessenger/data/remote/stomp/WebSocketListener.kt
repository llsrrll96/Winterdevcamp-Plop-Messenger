package com.plop.plopmessenger.data.remote.stomp

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plop.plopmessenger.data.local.entity.ChatRoom
import com.plop.plopmessenger.data.local.entity.Member
import com.plop.plopmessenger.data.local.entity.Message
import com.plop.plopmessenger.data.remote.stomp.constants.Commands
import com.plop.plopmessenger.data.remote.stomp.constants.Headers
import com.plop.plopmessenger.domain.model.MessageType
import com.plop.plopmessenger.domain.repository.ChatRoomRepository
import com.plop.plopmessenger.domain.repository.MemberRepository
import com.plop.plopmessenger.domain.repository.MessageRepository
import com.plop.plopmessenger.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import java.io.StringReader
import java.time.LocalDateTime
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

const val TERMINATE_MESSAGE_SYMBOL = "\u0000"
const val NORMAL_CLOSURE_STATUS = 1000
val PATTERN_HEADER = Pattern.compile("([^:\\s]+)\\s*:\\s*([^:\\s]+)")
const val socketUrl = "ws://3.39.130.186:8011/ws-chat"
const val stompHost = "stomp.github.org"
class WebSocketListener @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val chatRoomRepository: ChatRoomRepository,
    private val messageRepository: MessageRepository,
    private val memberRepository: MemberRepository,
    private val userRepository: UserRepository
) : WebSocketListener() {

    private val DEFAULT_ACK = "auto"
    private val SUPPORTED_VERSIONS = "1.1,1.2"

    private var shouldBeConnected: Boolean = false
    private var connected = false
    private lateinit var webSocket: WebSocket
    private var userId: String? = null

    fun connect() {
        if (!connected) {
            shouldBeConnected = true
            val request = Request.Builder()
                .url(socketUrl)
                .build()
            webSocket = okHttpClient.newWebSocket(request, this)
            connected = true
        }
    }

    fun reconnect() {
        if (shouldBeConnected) {
            close()
            Thread.sleep(300L)
            connect()
        }
    }
    fun close() {
        if (connected) {
            webSocket.close(1000, "")
            connected = false
        }
    }

    private fun handleMessage(message: SocketMessage) {
        when (message.command) {
            Commands.CONNECTED -> {
                Log.d("STOMP 로그", "연결되었다는 메세지")
            }
            Commands.MESSAGE -> {
                val jsonObject = JSONObject(message.payload)
                saveMessage(
                    Message(
                        chatroomId = jsonObject.getString("room_id"),
                        messageId = jsonObject.getString("message_id"),
                        messageFromID = jsonObject.getString("sender_id"),
                        content = jsonObject.getString("content"),
                        createdAt = LocalDateTime.now(),
                        type = 1
                    ))
            }
        }
    }

    fun join(topic: String) {
        val topicId = UUID.randomUUID().toString()

        val headers = HashMap<String, String>()
        headers[Headers.ID] = topicId
        headers[Headers.DESTINATION] = topic
        headers[Headers.ACK] = DEFAULT_ACK
        webSocket.send(compileMessage(SocketMessage(Commands.SUBSCRIBE, headers = headers)))
    }

    fun joinAll() {
        CoroutineScope(Dispatchers.IO).launch {
            chatRoomRepository.loadChatRoomIdList().forEach {
                join("/chatting/topic/room/${it}")
            }
            userRepository.getUserId().collect() {
                join("/chatting/topic/new-room/${it}")
                userId = it
            }
        }
    }


    fun saveMessage(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            if(!chatRoomRepository.hasChatRoomById(message.chatroomId)){
                saveNewChatRoom(chatRoomId = message.chatroomId, message)
            } else {
                updateChatRoom(message.chatroomId, message.content)
            }
            saveMembers(memberId = message.messageFromID, message.chatroomId, message)
            messageRepository.insertMessage(message)
        }

    }

    suspend fun saveNewChatRoom(chatRoomId: String, message: Message) {
        chatRoomRepository.insertChatRoom(
            ChatRoom(
                chatRoomId,
                message.messageFromID,
                1,
                message.content,
                message.createdAt,
                message.type
            )
        )
    }

    suspend fun saveMembers(memberId: String, chatRoomId: String, message: Message) {
        try {
            memberRepository.insertMember(
                Member(
                    memberId = memberId,
                    chatroomId = chatRoomId,
                    nickname = "",
                    profileImg = "",
                    readMessage = message.messageId
                )
            )
        } catch (e: Exception) {
            Log.d("SaveMember", e.message.toString())
        }
    }

    suspend fun updateChatRoom(roomId: String, content: String) {
        withContext(Dispatchers.IO) {
            chatRoomRepository.updateChatRoomContentById(roomId, content, LocalDateTime.now())
            chatRoomRepository.plusChatRoomUnreadById(roomId, 1)
        }
    }


    private fun compileMessage(message: SocketMessage): String {
        val builder = StringBuilder()

        if (message.command != null)
            builder.append(message.command).append('\n')

        for ((key, value) in message.headers)
            builder.append(key).append(':').append(value).append('\n')
        builder.append('\n')

        if (message.payload != null)
            builder.append(message.payload).append("\n\n")

        builder.append(TERMINATE_MESSAGE_SYMBOL)

        return builder.toString()
    }

    private fun parseMessage(data: String?): SocketMessage {

        if (data.isNullOrBlank())
            return SocketMessage(Commands.UNKNOWN)

        val reader = Scanner(StringReader(data))
        reader.useDelimiter("\\n")
        val command = reader.next()
        val headers = HashMap<String, String>()

        while (reader.hasNext(PATTERN_HEADER)) {
            val matcher = PATTERN_HEADER.matcher(reader.next())
            matcher.find()
            headers.put(matcher.group(1), matcher.group(2))
        }

        reader.skip("\\s")

        reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL)
        val payload = if (reader.hasNext()) reader.next() else null

        return SocketMessage(command, headers, payload!!)
    }

    override fun onOpen(socket: WebSocket, response: Response) {
        val headers = HashMap<String, String>()
        headers[Headers.VERSION] = SUPPORTED_VERSIONS
        headers[Headers.HOST] = stompHost
        webSocket.send(compileMessage(SocketMessage(Commands.CONNECT, headers)))
        webSocket = socket
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        handleMessage(parseMessage(bytes.toString()))
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        handleMessage(parseMessage(text))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("STOMP 로그", "onFailure 실행 ${response}")
        reconnect()
    }
}