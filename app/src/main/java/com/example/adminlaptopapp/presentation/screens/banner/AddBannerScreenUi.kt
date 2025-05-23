package com.example.adminlaptopapp.presentation.screens.banner

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.utils.CustomTextField
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddBannerScreenUi(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel()
) {

    val addBannerState = viewModel.addBannerState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            image.value = uri.toString()
        }
    }

    LaunchedEffect(
        addBannerState.value.isLoading,
        addBannerState.value.errorMessage,
        addBannerState.value.bannerData
    ) {
        if (addBannerState.value.isLoading) {
            Toast.makeText(context, "Đang thêm banner...", Toast.LENGTH_SHORT).show()
        } else if (addBannerState.value.errorMessage != null) {
            Toast.makeText(context, addBannerState.value.errorMessage ?: "Lỗi không xác định", Toast.LENGTH_SHORT).show()
        } else if (addBannerState.value.bannerData != null) {
            Toast.makeText(context, "Thêm Banner thành công", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.GetAllBannerScreen)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Thêm Banner") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (addBannerState.value.isLoading) {
            // Hiển thị loading khi đang chờ
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ){
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = "Tên Banner",
                    leadingIcon = Icons.Default.Edit,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Button chọn ảnh
                Button(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.orange), // nền cam
                        contentColor = Color.White // màu chữ/trên icon
                    )
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Chọn ảnh")
                    Spacer(Modifier.width(8.dp))
                    Text("Chọn hình ảnh")
                }

                Spacer(Modifier.height(16.dp))

                // Hiển thị ảnh sau khi chọn
                image.value?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Ảnh đã chọn",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        val banner = BannerDataModels(
                            name = name.value,
                            image = image.value!!,
                            date = SimpleDateFormat("dd/MM/yyyy", getDefault()).format(System.currentTimeMillis())
                        )

                        if(name.value.isNotEmpty()){
                            viewModel.addBanner(banner)
                        }else{
                            Toast.makeText(context, "Vui lòng nhập tên Banner", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.orange), // nền cam
                        contentColor = Color.White // màu chữ/trên icon
                    )
                ) {
                    Icon(
                        Icons.Default.Slideshow,
                        contentDescription = "Thêm Banner"
                    )
                    Text("Thêm Banner")
                }

            }
        }
    }


}