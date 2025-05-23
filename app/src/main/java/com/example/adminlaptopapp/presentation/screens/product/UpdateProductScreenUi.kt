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
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.utils.CustomTextField
import com.example.adminlaptopapp.presentation.utils.formatPrice
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdateProductScreenUi(
    navController: NavController,
    productId: String,
    viewModel: AdminLaptopViewModel = hiltViewModel()
){
    val updateProductState = viewModel.updateProductState.collectAsStateWithLifecycle()
    val getProductById = viewModel.getProductByIdState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    // Nhận các giá trị từ viewModel
    val nameUp = remember { mutableStateOf("") }
    val priceUp = remember { mutableStateOf("") }
    val shortDescUp = remember { mutableStateOf("") }
    val longDescUp = remember { mutableStateOf("") }
    val quantityUp = remember { mutableStateOf("") }
    val targetUp = remember { mutableStateOf("") }
    val categoryUp = remember { mutableStateOf("") }
    val statusUp = remember { mutableStateOf("") }
    val imageUp = remember { mutableStateOf<String?>(null) }

    var expandedCategoryUp by remember { mutableStateOf(false) }
    var expandedStatusUp by remember { mutableStateOf(false) }

    val categoryState = viewModel.getAllCategoryState.collectAsStateWithLifecycle()
    val statusList = listOf("Vẫn còn", "Đã hết")

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllCategory()
        viewModel.getProductById(productId)
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUp.value = uri.toString()
        }
    }

    // Lắng nghe trạng thái
    LaunchedEffect(
        updateProductState.value.isLoading,
        updateProductState.value.errorMessage,
        updateProductState.value.productData
    ) {
        if (updateProductState.value.isLoading) {
            Toast.makeText(context, "Đang cập nhật sản phẩm...", Toast.LENGTH_SHORT).show()
        } else if (updateProductState.value.errorMessage != null) {
            Toast.makeText(context, updateProductState.value.errorMessage ?: "Lỗi không xác định", Toast.LENGTH_SHORT).show()
        } else if (updateProductState.value.productData != null) {
            Toast.makeText(context, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.GetAllProductScreen)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ){
       innerPadding ->

        if (updateProductState.value.isLoading) {
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

            when {
                getProductById.value.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                getProductById.value.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = getProductById.value.errorMessage!!)
                    }
                }

                getProductById.value.productData != null -> {
                    val product = getProductById.value.productData!!.copy(productId = productId)

                    // Set giá trị lần đầu khi có dữ liệu
                    LaunchedEffect(product) {
                        nameUp.value = product.name
                        priceUp.value = formatCurrencyVND(product.price)
                        shortDescUp.value = product.short_desc
                        longDescUp.value = product.long_desc
                        quantityUp.value = product.quantity.toString()
                        targetUp.value = product.target
                        categoryUp.value = product.category
                        statusUp.value = product.status
                        imageUp.value = product.image
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        CustomTextField(
                            value = nameUp.value,
                            onValueChange = { nameUp.value = it },
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
                                value = priceUp.value,
                                onValueChange = { priceUp.value = it },
                                label = "Giá",
                                leadingIcon = Icons.Default.Edit,
                                modifier = Modifier.weight(1f),
                            )
                            CustomTextField(
                                value = quantityUp.value,
                                onValueChange = { quantityUp.value = it },
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
                                value = shortDescUp.value,
                                onValueChange = { shortDescUp.value = it },
                                label = "Mô tả ngắn",
                                leadingIcon = Icons.Default.Edit,
                                modifier = Modifier.weight(1f),
                            )
                            CustomTextField(
                                value = targetUp.value,
                                onValueChange = { targetUp.value = it },
                                label = "Hãng sản phẩm",
                                leadingIcon = Icons.Default.Edit,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CustomTextField(
                            value = longDescUp.value,
                            onValueChange = { longDescUp.value = it },
                            label = "Mô tả chi tiết",
                            leadingIcon = Icons.Default.Edit,
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dropdown chọn chuyên mục
                        ExposedDropdownMenuBox(
                            expanded = expandedCategoryUp,
                            onExpandedChange = { expandedCategoryUp = !expandedCategoryUp },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = categoryUp.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Chuyên mục sản phẩm") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expandedCategoryUp
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategoryUp,
                                onDismissRequest = { expandedCategoryUp = false }
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
                                                    categoryUp.value = item.name ?: ""
                                                    expandedCategoryUp = false
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
                            expanded = expandedStatusUp,
                            onExpandedChange = { expandedStatusUp = !expandedStatusUp },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = statusUp.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Trạng thái sản phẩm") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expandedStatusUp
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStatusUp,
                                onDismissRequest = { expandedStatusUp = false }
                            ) {
                                statusList.forEach { statusItem ->
                                    DropdownMenuItem(
                                        text = { Text(statusItem) },
                                        onClick = {
                                            statusUp.value = statusItem
                                            expandedStatusUp = false
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
                        imageUp.value?.let { uri ->
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
                                val updatedProduct = ProductDataModels(
                                    productId = productId,
                                    name = nameUp.value,
                                    price = priceUp.value.toDouble(),
                                    short_desc = shortDescUp.value,
                                    long_desc = longDescUp.value,
                                    quantity = quantityUp.value.toInt(),
                                    target = targetUp.value,
                                    category = categoryUp.value,
                                    status = statusUp.value,
                                    image = imageUp.value ?: product.image
                                )
                                viewModel.updateProduct(updatedProduct, productId)
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
                                Icons.Default.Edit,
                                contentDescription = "Cập nhật sản phẩm"
                            )
                            Text("Cập nhật sản phẩm")
                        }
                    }
                }
            }
        }
    }
}