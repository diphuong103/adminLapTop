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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.domain.models.ChatMessage
import com.example.adminlaptopapp.presentation.viewModels.ChatViewModel
import kotlinx.coroutines.launch
import java.util.*
import com.example.adminlaptopapp.utils.formatTimestamp


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
    onClearSelectedImage: () -> Unit,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentUserId) {
        chatViewModel.loadMessages(currentUserId)
    }

    val messages by chatViewModel.messages.collectAsState()
    val otherUser by chatViewModel.otherUser.collectAsState()

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
                            model = if (userImageUrl.isNotBlank()) userImageUrl else R.drawable.default_avatar,
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
                        isMe = message.senderId == "admin" // Admin messages on right
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
}

@Composable
fun MessageBubble(message: ChatMessage, isMe: Boolean) {

    val timeText = formatTimestamp(timestamp = message.timestamp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Image(
                painter = painterResource(id = R.drawable.default_avatar),
                contentDescription = "Client Avatar",
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
                    MaterialTheme.colorScheme.primary  // Admin messages
                else
                    MaterialTheme.colorScheme.surfaceVariant  // Client messages
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (message.urlIMG.isNotBlank()) {
                Column {
                    Image(
                        painter = rememberAsyncImagePainter(model = message.urlIMG),
                        contentDescription = "Hình ảnh đã gửi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    if (message.text.isNotBlank()) {
                        Text(
                            text = message.text,
                            color = if (isMe)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

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
                }
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

        // Add spacing on the right for admin messages
        if (isMe) {
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}




