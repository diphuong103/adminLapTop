package com.example.adminlaptopapp.presentation.screens.Chat

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopapp.domain.models.ChatMessage
import com.example.adminlaptopapp.presentation.viewModels.ChatViewModel
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

@Composable
fun ChatScreenRoute(
    userId: String,
    chatRoomId: String,
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val messages by chatViewModel.messages.collectAsState()
    val otherUser by chatViewModel.otherUser.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

//    // Initialize chat room
//    LaunchedEffect(chatRoomId) {
//        chatViewModel.initializeChatRoom(chatRoomId)
//    }

    ChatScreen(
        userName = otherUser?.displayName ?: "Unknown User",
        userImageUrl = otherUser?.avatarUrl ?: "",
        messages = messages,
        currentUserId = userId,
        onBackPressed = { navController.navigateUp() },
        onSendMessage = { text, imageUri ->
            chatViewModel.sendMessage(text, imageUri?.toString())
            selectedImageUri = null
        },
        onSelectImage = { launcher.launch("image/*") },
        selectedImageUri = selectedImageUri,
        onClearSelectedImage = { selectedImageUri = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    userName: String,
    userImageUrl: String,
    messages: List<ChatMessage>,
    currentUserId: String,
    onBackPressed: () -> Unit,
    onSendMessage: (text: String, imageUri: Uri?) -> Unit,
    onSelectImage: () -> Unit,
    selectedImageUri: Uri?,
    onClearSelectedImage: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = userImageUrl.ifBlank { null },
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = userName, fontWeight = FontWeight.Medium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        isMe = message.senderId == currentUserId
                    )
                }
            }

            // Selected image preview
            selectedImageUri?.let { uri ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Selected Image Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Close button
                        IconButton(
                            onClick = onClearSelectedImage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove Image",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Message input area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Image selection button
                    IconButton(
                        onClick = onSelectImage,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Filled.Photo,
                            contentDescription = "Select Image",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nhập tin nhắn...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank() || selectedImageUri != null) {
                                onSendMessage(inputText.trim(), selectedImageUri)
                                inputText = ""
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }

    @Composable
    fun MessageBubble(message: ChatMessage, isMe: Boolean) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
        ) {
            Card(
                modifier = Modifier.widthIn(max = 280.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isMe) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMe) 16.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 16.dp
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Image message
                    if (message.urlIMG.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(message.urlIMG),
                            contentDescription = "Image message",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        if (message.text.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Text message
                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            color = if (isMe) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    // Timestamp
                    Text(
                        text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(message.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.End),
                        color = if (isMe) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, isMe: Boolean) {
    val timeText = formatTimestamp(message.timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isMe) Arrangement.Start else Arrangement.End
    ) {
        if (!isMe) {
            Image(
                painter = rememberAsyncImagePainter("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSHavAqEKhY8MRX7NntKRnkGqFTk42uJT_TuA&s"),
                contentDescription = "Support Avatar",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (message.urlIMG.isNotBlank()) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(model = message.urlIMG),
                        contentDescription = "Hình ảnh đã gửi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // Nếu muốn overlay text trên ảnh có thể thêm ở đây
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = timeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMe)
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .align(Alignment.End)
                )

            } else {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            color = if (isMe)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMe)
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        if (isMe) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


fun uploadImageToImgBB(context: Context, uri: Uri, onSuccess: (String) -> Unit) {
    val apiKey = "cfb88fd6087fa222b489b186dff8c38d"

    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes()
    val encoded = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

    val client = OkHttpClient()
    val requestBody = FormBody.Builder()
        .add("key", apiKey)
        .add("image", encoded)
        .build()

    val request = Request.Builder()
        .url("https://api.imgbb.com/1/upload")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {}

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { body ->
                val json = JSONObject(body)
                val url = json.getJSONObject("data").getString("url")
                onSuccess(url)
            }
        }
    })
}
