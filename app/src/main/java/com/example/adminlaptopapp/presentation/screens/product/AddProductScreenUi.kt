package com.example.adminlaptopapp.presentation.screens.product

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.utils.CustomTextField
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddProductScreenUi(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel()
) {
    val addProductState = viewModel.addProductState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    // Nhận các giá trị từ viewModel
    val name = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val shortDesc = remember { mutableStateOf("") }
    val longDesc = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("") }
    val target = remember { mutableStateOf("") }
    val category = remember { mutableStateOf("") }
    val status = remember { mutableStateOf("") }
    val image = remember { mutableStateOf<String?>(null) }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }

    val categoryState = viewModel.getAllCategoryState.collectAsStateWithLifecycle()
    val statusList = listOf("Vẫn còn", "Đã hết")

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllCategory()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            image.value = uri.toString()
        }
    }

    // Lắng nghe trạng thái
    LaunchedEffect(
        addProductState.value.isLoading,
        addProductState.value.errorMessage,
        addProductState.value.productData
    ) {
        if (addProductState.value.isLoading) {
            Toast.makeText(context, "Đang thêm sản phẩm...", Toast.LENGTH_SHORT).show()
        } else if (addProductState.value.errorMessage != null) {
            Toast.makeText(context, addProductState.value.errorMessage ?: "Lỗi không xác định", Toast.LENGTH_SHORT).show()
        } else if (addProductState.value.productData != null) {
            Toast.makeText(context, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.GetAllProductScreen)
        }
    }




    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Thêm sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (addProductState.value.isLoading) {
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
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = "Tên sản phẩm",
                    leadingIcon = Icons.Default.Edit,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = price.value,
                        onValueChange = { price.value = it },
                        label = "Giá",
                        leadingIcon = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                    )
                    CustomTextField(
                        value = quantity.value,
                        onValueChange = { quantity.value = it },
                        label = "Số lượng",
                        leadingIcon = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = shortDesc.value,
                        onValueChange = { shortDesc.value = it },
                        label = "Mô tả ngắn",
                        leadingIcon = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                    )
                    CustomTextField(
                        value = target.value,
                        onValueChange = { target.value = it },
                        label = "Hãng sản phẩm",
                        leadingIcon = Icons.Default.Edit,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    value = longDesc.value,
                    onValueChange = { longDesc.value = it },
                    label = "Mô tả chi tiết",
                    leadingIcon = Icons.Default.Edit,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown chọn chuyên mục
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = category.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chuyên mục sản phẩm") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        if (categoryState.value.isLoading) {
                            DropdownMenuItem(
                                text = { Text("Đang tải...") },
                                onClick = { }
                            )
                        } else {
                            categoryState.value.categoriesData.forEach { categoryItem ->
                                categoryItem?.let { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.name ?: "Không tên") },
                                        onClick = {
                                            category.value = item.name ?: ""
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown chọn trạng thái
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = status.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Trạng thái sản phẩm") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStatus) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statusList.forEach { statusItem ->
                            DropdownMenuItem(
                                text = { Text(statusItem) },
                                onClick = {
                                    status.value = statusItem
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

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

                // Button thêm sản phẩm
                Button(
                    onClick = {
                        val product = ProductDataModels(
                            productId = "",
                            name = name.value,
                            price = price.value.toDouble(),
                            short_desc = shortDesc.value,
                            long_desc = longDesc.value,
                            quantity = quantity.value.toInt(),
                            target = target.value,
                            category = category.value,
                            status = status.value,
                            image = image.value!!,
                            date = SimpleDateFormat("dd/MM/yyyy", getDefault()).format(System.currentTimeMillis()),
                            createBy = "admin"
                        )

                        if(name.value.isEmpty() || price.value.isEmpty() || image.value!!.isEmpty()){
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        }else{
                            viewModel.addProduct(product)
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
                    Text("Thêm sản phẩm")
                }

            }

        }


    }
}
