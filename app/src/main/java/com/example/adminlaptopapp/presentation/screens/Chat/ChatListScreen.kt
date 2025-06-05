package com.example.adminlaptopapp.presentation.screens.Chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.adminlaptopapp.R
import java.text.SimpleDateFormat
import java.util.*

data class ChatListItem(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserAvatarUrl: String,
    val lastMessage: String,
    val hasUnreadMessages: Boolean,
    val lastMessageTimestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatListScreen(
    chatItems: List<ChatListItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onChatClicked: (chatId: String, otherUserId: String) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onRetry: () -> Unit = {},
    onClearError: () -> Unit = {}
) {


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Danh sách Chat Client", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
             innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Tìm kiếm người dùng") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    // Handle search action if needed
                }),
                colors = TextFieldDefaults.outlinedTextFieldColors(),
                enabled = !isLoading
            )

            // Error handling
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Có lỗi xảy ra",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            TextButton(
                                onClick = {
                                    onClearError()
                                    onRetry()
                                }
                            ) {
                                Text("Thử lại")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = onClearError) {
                                Text("Đóng")
                            }
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Đang tải danh sách chat...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }

                else -> {
                    // Filter list by search query
                    val filteredItems = remember(searchQuery, chatItems) {
                        if (searchQuery.isBlank()) chatItems
                        else chatItems.filter {
                            it.otherUserName.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredItems.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) {
                                        "Chưa có cuộc trò chuyện nào"
                                    } else {
                                        "Không tìm thấy cuộc trò chuyện phù hợp"
                                    },
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                if (searchQuery.isBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Bắt đầu cuộc trò chuyện mới bằng cách tìm kiếm người dùng",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = onRetry,
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Làm mới")
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(filteredItems) { chat ->
                                ChatListItemView(
                                    chat = chat,
                                    onClick = { onChatClicked(chat.chatId, chat.otherUserId) }
                                )
                                if (chat != filteredItems.last()) {
                                    Divider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = Color.Gray.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItemView(chat: ChatListItem, onClick: () -> Unit) {
    val backgroundColor = if (chat.hasUnreadMessages) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chat.otherUserAvatarUrl.takeIf { it.isNotBlank() } ?: R.drawable.default_avatar,
            contentDescription = "Ảnh đại diện ${chat.otherUserName}",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.otherUserName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (chat.hasUnreadMessages) FontWeight.Bold else FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (chat.lastMessageTimestamp > 0) {
                    Text(
                        text = formatTimestamp(chat.lastMessageTimestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (chat.hasUnreadMessages) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Gray
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (chat.hasUnreadMessages) FontWeight.Medium else FontWeight.Normal
                    ),
                    maxLines = 1,
                    color = if (chat.hasUnreadMessages) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        Color.Gray
                    },
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (chat.hasUnreadMessages) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Vừa xong" // Less than 1 minute
        diff < 3600_000 -> "${diff / 60_000}p" // Less than 1 hour, show minutes
        diff < 86400_000 -> "${diff / 3600_000}h" // Less than 1 day, show hours
        diff < 604800_000 -> { // Less than 1 week, show day of week
            SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
        }
        else -> { // More than 1 week, show date
            SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
        }
    }
}