package com.example.adminlaptopapp.presentation.screens.user

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GetUserByEmailScreenUi(
    navController: NavController,
    email: String,
    viewModel: AdminLaptopViewModel = hiltViewModel()
) {
    val getUserByEmail = viewModel.getUserByEmailState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.getUserByEmail(email)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    val user = getUserByEmail.value.userData?.userData
                    Text(
                        text = if (user != null) "${user.firstName} ${user.lastName}" else "Thông tin người dùng",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        when {
            getUserByEmail.value.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            getUserByEmail.value.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getUserByEmail.value.errorMessage!!)
                }
            }

            getUserByEmail.value.userData != null -> {
                val user = getUserByEmail.value.userData!!.userData.copy(email = email)

                val checkFirstName = if (user.firstName.isBlank()) "Chưa điền" else user.firstName
                val checkLastName = if (user.lastName.isBlank()) "Chưa điền" else user.lastName
                val checkFullName = "$checkFirstName $checkLastName"
                val checkPhone = if (user.phoneNumber.isBlank()) "Chưa điền" else user.phoneNumber
                val checkAddress = if (user.address.isBlank()) "Chưa điền" else user.address
                val checkImage = if (user.profileImage.isEmpty()) Icons.Default.Person else user.profileImage

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, colorResource(id = R.color.orange), CircleShape)
                            .padding(8.dp)
                    ) {
                        if (user.profileImage.isNullOrBlank()) {
                            // Dùng ImageVector khi không có ảnh
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Dùng AsyncImage khi có URL
                            AsyncImage(
                                model = user.profileImage,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Table-style rows
                    Column(
                        modifier = Modifier.padding(start = 24.dp , end = 24.dp)
                    ) {
                        InfoRow(label = "Họ", value = checkFirstName)
                        InfoRow(label = "Tên", value = checkLastName)
                        InfoRow(label = "Họ và tên", value = checkFullName)
                        InfoRow(label = "Email", value = email)
                        InfoRow(label = "Số điện thoại", value = checkPhone)
                        InfoRow(label = "Địa chỉ", value = checkAddress)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.exportUserToPdf(context, user)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export PDF")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Xuất PDF người dùng")
                    }

                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "$label:",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
