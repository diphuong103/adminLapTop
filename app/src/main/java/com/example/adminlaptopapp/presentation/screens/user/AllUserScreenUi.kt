package com.example.adminlaptopapp.presentation.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.domain.models.UserData
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.screens.product.ProductCard
import com.example.adminlaptopapp.presentation.screens.product.SmallIconButton
import com.example.adminlaptopapp.presentation.screens.product.formatCurrencyVND
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllUserScreenUi(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel(),
) {

    val getAllUserState = viewModel.getAllUserState.collectAsStateWithLifecycle()
    val userData = getAllUserState.value.usersData ?: emptyList()

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllUser()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filteredUser by remember { mutableStateOf(userData) }

    // Thực thi tìm kiếm khi searchQuery thay đổi
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            // Tìm kiếm các user theo từ khóa
            filteredUser = userData.filter { product ->
                // kiểm tra firstname , lastname , email , numberPgone
                product?.userData?.firstName?.contains(searchQuery, ignoreCase = true) == true ||
                        product?.userData?.lastName?.contains(searchQuery, ignoreCase = true) == true ||
                        product?.userData?.email?.contains(searchQuery, ignoreCase = true) == true ||
                        product?.userData?.phoneNumber?.contains(searchQuery, ignoreCase = true) == true
            }
        } else {
            // Nếu không có từ khóa tìm kiếm, hiển thị tất cả sản phẩm
            filteredUser = userData
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Tất cả người dùng", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                    },
                    modifier = Modifier
                        .padding(8.dp),
                    placeholder = { Text("Tìm kiếm") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

//                IconButton(
//                    onClick = {
////                        navController.navigate(Routes.AddProductScreen)
//                    },
//                    modifier = Modifier
//                        .background(color = colorResource(id = R.color.orange), shape = CircleShape)
//                        .align(Alignment.CenterVertically)
//                        .padding(end = 8.dp)
//
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = null,
//                        modifier = Modifier.size(30.dp)
//                    )
//                }
            }


            when{
                getAllUserState.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                getAllUserState.value.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Xin lỗi, không thể lấy được thông tin người dùng.")
                    }
                }

                userData.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Không có người dùng nào cả")
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                        ) {
                            Text(
                                text = "Ảnh đại diện",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(0.45f))
                            Text(
                                text = "Thông tin user",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(0.1f))
                            Text(
                                text = "Chức năng",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterVertically),
                            )
                            Spacer(modifier = Modifier.weight(.15f))
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (filteredUser.isEmpty()) {
                                items(userData) { item ->
                                    if (item != null) {
                                        UserItemCard(
                                            user = item.userData,
                                            onShowDetailsClick = {
                                                navController.navigate(Routes.GetUserByEmailScreen(item.userData.email))
                                            },
                                            onDeleteClick = {
                                                viewModel.deleteUser(item.userData.email)
                                            }
                                        )
                                    }
                                }
                            } else {
                                items(filteredUser) { item ->
                                    if (item != null) {
                                        UserItemCard(
                                            user = item.userData,
                                            onShowDetailsClick = {
                                                navController.navigate(Routes.GetUserByEmailScreen(item.userData.email))
                                            },
                                            onDeleteClick = {
                                                viewModel.deleteUser(item.userData.email)
                                            }
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

}

@Composable
fun UserItemCard(
    user: UserData,
    onShowDetailsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user.profileImage.isNullOrBlank()) {
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = user.lastName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            } else {
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = user.lastName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = user.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.height(120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                SmallIconButton(
                    onClick = onShowDetailsClick,
                    backgroundColor = Color(0xFF4CAF50),
                    icon = Icons.Default.RemoveRedEye,
                    contentDescription = "Xem"
                )

                SmallIconButton(
                    onClick = { showDeleteDialog = true },
                    backgroundColor = Color(0xFFF44336),
                    icon = Icons.Default.Delete,
                    contentDescription = "Xóa"
                )
            }
        }
    }

    // Hiển thị AlertDialog xác nhận xóa
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa người ${user.lastName} ${user.firstName} không?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteClick()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Hủy")
                }
            }
        )
    }
}
