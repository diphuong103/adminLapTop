package com.example.adminlaptopapp.presentation.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminlaptopapp.domain.models.ChatMessage
import com.example.adminlaptopapp.domain.models.UserChat
import com.example.adminlaptopapp.presentation.screens.Chat.ChatListItem
import com.example.adminlaptopapp.presentation.utils.ImageUploadHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("ChatViewModel", "ViewModel created")
    }

    private val _chatList = MutableStateFlow<List<ChatListItem>>(emptyList())
    val chatList: StateFlow<List<ChatListItem>> = _chatList

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _otherUser = MutableStateFlow<UserChat?>(null)
    val otherUser: StateFlow<UserChat?> = _otherUser

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Thêm state cho việc upload hình ảnh
    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage

    private val db = FirebaseDatabase.getInstance().reference
    private var currentChatWithUserId: String = ""
    private var messagesListener: ValueEventListener? = null
    private val imageUploadHelper = ImageUploadHelper()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadChatList() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("ChatViewModel", "Starting to load chat list...")

                val chatsRef = db.child("chats")

                chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("ChatViewModel", "Chats snapshot exists: ${snapshot.exists()}")
                        Log.d("ChatViewModel", "Number of chats: ${snapshot.childrenCount}")

                        if (!snapshot.exists()) {
                            Log.d("ChatViewModel", "No chats found")
                            _chatList.value = emptyList()
                            _isLoading.value = false
                            return
                        }

                        val tempList = mutableListOf<ChatListItem>()
                        val clientIds = snapshot.children.toList()
                        var processedCount = 0

                        if (clientIds.isEmpty()) {
                            Log.d("ChatViewModel", "Client IDs list is empty")
                            _chatList.value = emptyList()
                            _isLoading.value = false
                            return
                        }

                        Log.d("ChatViewModel", "Processing ${clientIds.size} chats")

                        clientIds.forEach { clientChatSnap ->
                            val clientId = clientChatSnap.key
                            Log.d("ChatViewModel", "Processing chat for client: $clientId")

                            if (clientId == null) {
                                processedCount++
                                checkIfAllProcessed(processedCount, clientIds.size, tempList)
                                return@forEach
                            }

                            // Kiểm tra xem có tin nhắn nào không
                            val messagesSnap = clientChatSnap.child("messages")
                            if (!messagesSnap.exists()) {
                                Log.d("ChatViewModel", "No messages found for client: $clientId")
                                processedCount++
                                checkIfAllProcessed(processedCount, clientIds.size, tempList)
                                return@forEach
                            }

                            Log.d("ChatViewModel", "Found ${messagesSnap.childrenCount} messages for client: $clientId")

                            // Lấy thông tin user từ node users
                            db.child("users").child(clientId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(userSnap: DataSnapshot) {
                                        Log.d("ChatViewModel", "User data exists for $clientId: ${userSnap.exists()}")

                                        val firstName = userSnap.child("firstName").getValue(String::class.java) ?: ""
                                        val lastName = userSnap.child("lastName").getValue(String::class.java) ?: ""
                                        val email = userSnap.child("email").getValue(String::class.java) ?: ""

                                        val displayName = when {
                                            firstName.isNotEmpty() && lastName.isNotEmpty() -> "$firstName $lastName"
                                            firstName.isNotEmpty() -> firstName
                                            email.isNotEmpty() -> email
                                            else -> "Khách hàng"
                                        }

                                        val avatarUrl = userSnap.child("profileImage").getValue(String::class.java) ?: ""

                                        Log.d("ChatViewModel", "User info - Name: $displayName, Avatar: $avatarUrl")

                                        // Tìm tin nhắn cuối cùng
                                        val messagesList = messagesSnap.children.toList()
                                        var lastMessage: ChatMessage? = null
                                        var lastTimestamp = 0L
                                        var hasUnread = false

                                        messagesList.forEach { messageSnap ->
                                            val message = messageSnap.getValue(ChatMessage::class.java)
                                            if (message != null) {
                                                // Tìm tin nhắn cuối cùng
                                                if (message.timestamp > lastTimestamp) {
                                                    lastTimestamp = message.timestamp
                                                    lastMessage = message
                                                }

                                                // Kiểm tra tin nhắn chưa đọc từ client
                                                if (message.senderId == clientId && message.isRead == false) {
                                                    hasUnread = true
                                                }
                                            }
                                        }

                                        val lastMessageText = when {
                                            !lastMessage?.text.isNullOrBlank() -> lastMessage?.text ?: ""
                                            !lastMessage?.urlIMG.isNullOrBlank() -> "📷 Hình ảnh"
                                            else -> "Không có tin nhắn"
                                        }

                                        Log.d("ChatViewModel", "Last message: $lastMessageText, Unread: $hasUnread")

                                        val chatItem = ChatListItem(
                                            chatId = clientId,
                                            otherUserId = clientId,
                                            otherUserName = displayName,
                                            otherUserAvatarUrl = avatarUrl,
                                            lastMessage = lastMessageText,
                                            hasUnreadMessages = hasUnread,
                                            lastMessageTimestamp = lastTimestamp
                                        )

                                        tempList.add(chatItem)
                                        processedCount++

                                        Log.d("ChatViewModel", "Added chat item. Processed: $processedCount/${clientIds.size}")
                                        checkIfAllProcessed(processedCount, clientIds.size, tempList)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e("ChatViewModel", "Error loading user data for $clientId", error.toException())
                                        processedCount++
                                        checkIfAllProcessed(processedCount, clientIds.size, tempList)
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ChatViewModel", "Error loading chat list", error.toException())
                        _error.value = "Lỗi khi tải danh sách chat: ${error.message}"
                        _isLoading.value = false
                    }
                })
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception in loadChatList", e)
                _error.value = "Lỗi không xác định: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private fun checkIfAllProcessed(processedCount: Int, totalCount: Int, tempList: MutableList<ChatListItem>) {
        if (processedCount == totalCount) {
            val sortedList = tempList.sortedByDescending { it.lastMessageTimestamp }
            _chatList.value = sortedList
            _isLoading.value = false
            Log.d("ChatViewModel", "Chat list loaded successfully with ${sortedList.size} items")
        }
    }

    fun loadMessages(clientId: String) {
        currentChatWithUserId = clientId
        Log.d("ChatViewModel", "Loading messages for client: $clientId")

        // Load thông tin client
        db.child("users").child(clientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                    val lastName = snapshot.child("lastName").getValue(String::class.java) ?: ""
                    val profileImage = snapshot.child("profileImage").getValue(String::class.java) ?: ""

                    val displayName = if (firstName.isNotEmpty()) {
                        if (lastName.isNotEmpty()) "$firstName $lastName" else firstName
                    } else {
                        snapshot.child("email").getValue(String::class.java) ?: "Khách hàng"
                    }

                    _otherUser.value = UserChat(
                        uid = clientId,
                        displayName = displayName,
                        avatarUrl = profileImage
                    )

                    Log.d("ChatViewModel", "Other user loaded: $displayName")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatViewModel", "Error loading user info", error.toException())
                }
            })

        // Remove listener cũ nếu có
        messagesListener?.let {
            db.child("chats").child(currentChatWithUserId).child("messages").removeEventListener(it)
        }

        // Load tin nhắn với client này
        messagesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messagesList = mutableListOf<ChatMessage>()
                snapshot.children.forEach { messageSnap ->
                    val message = messageSnap.getValue(ChatMessage::class.java)
                    if (message != null) {
                        messagesList.add(message)
                    }
                }
                _messages.value = messagesList.sortedBy { it.timestamp }
                Log.d("ChatViewModel", "Messages loaded: ${messagesList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatViewModel", "Error loading messages", error.toException())
            }
        }

        db.child("chats").child(clientId).child("messages")
            .addValueEventListener(messagesListener!!)

        // Đánh dấu tin nhắn từ client đã được đọc
        markMessagesAsRead(clientId)
    }

    fun markMessagesAsRead(clientId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: adminId

        db.child("chats").child(clientId).child("messages")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (msgSnap in snapshot.children) {
                        val message = msgSnap.getValue(ChatMessage::class.java)
                        if (message != null
                            && message.senderId == clientId
                            && message.isRead == false
                        ) {
                            msgSnap.ref.child("isRead").setValue(true)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatViewModel", "Error marking messages as read", error.toException())
                }
            })
    }

    private val adminId = "admin"

    // Cập nhật hàm sendMessage để upload hình ảnh trước khi gửi
    fun sendMessage(context: Context, text: String, imageUri: Uri?) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: adminId
        if (currentChatWithUserId.isEmpty()) return
        if (text.isBlank() && imageUri == null) return

        viewModelScope.launch {
            try {
                var imageUrl: String? = null

                // Nếu có hình ảnh, upload lên ImgBB trước
                if (imageUri != null) {
                    _isUploadingImage.value = true

                    val uploadResult = imageUploadHelper.uploadImageToImgBB(context, imageUri)

                    if (uploadResult.isSuccess) {
                        imageUrl = uploadResult.getOrNull()
                        Log.d("ChatViewModel", "Image uploaded successfully: $imageUrl")
                    } else {
                        Log.e("ChatViewModel", "Image upload failed", uploadResult.exceptionOrNull())
                        _error.value = "Lỗi khi upload hình ảnh: ${uploadResult.exceptionOrNull()?.message}"
                        _isUploadingImage.value = false
                        return@launch
                    }

                    _isUploadingImage.value = false
                }

                // Gửi tin nhắn với URL hình ảnh (nếu có)
                val messageId = db.child("chats").child(currentChatWithUserId).child("messages").push().key ?: return@launch

                val message = ChatMessage(
                    id = messageId,
                    senderId = currentUserId,
                    text = text.trim(),
                    urlIMG = imageUrl ?: "",
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )

                db.child("chats").child(currentChatWithUserId).child("messages").child(messageId)
                    .setValue(message)
                    .addOnSuccessListener {
                        Log.d("ChatViewModel", "Message sent successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ChatViewModel", "Error sending message", e)
                        _error.value = "Lỗi khi gửi tin nhắn: ${e.message}"
                    }

            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception in sendMessage", e)
                _error.value = "Lỗi không xác định: ${e.message}"
                _isUploadingImage.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        messagesListener?.let {
            if (currentChatWithUserId.isNotEmpty()) {
                db.child("chats").child(currentChatWithUserId).child("messages").removeEventListener(it)
            }
        }
    }
}