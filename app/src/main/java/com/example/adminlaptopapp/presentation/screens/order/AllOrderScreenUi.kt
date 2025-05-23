package com.example.adminlaptopapp.presentation.screens.order

import android.annotation.SuppressLint
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
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.screens.product.ProductCard
import com.example.adminlaptopapp.presentation.screens.product.SmallIconButton
import com.example.adminlaptopapp.presentation.screens.product.formatCurrencyVND
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllOrderScreenUi(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel(),
) {
    val getAllOrdersState = viewModel.getAllOrderState.collectAsStateWithLifecycle()
    val orderData = getAllOrdersState.value.ordersData ?: emptyList()

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllOrder()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filteredOrders by remember { mutableStateOf(orderData) }

    // Thực thi tìm kiếm khi searchQuery thay đổi
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            // Tìm kiếm các sản phẩm theo từ khóa
            filteredOrders = orderData.filter { product ->
                // Kiểm tra nếu tên, thể loại, giá, hoặc số lượng chứa từ khóa
                product?.name?.contains(searchQuery, ignoreCase = true) == true ||
                        formatCurrencyVND(product?.totalPrice ?: 0).contains(searchQuery, ignoreCase = true) ||
                        product?.postalCode?.contains(searchQuery, ignoreCase = true) == true
            }
        } else {
            // Nếu không có từ khóa tìm kiếm, hiển thị tất cả sản phẩm
            filteredOrders = orderData
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Tất cả sản phẩm", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
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
                        .fillMaxWidth()
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
            }

            when{
                getAllOrdersState.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                getAllOrdersState.value.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Xin lỗi, không thể lấy được thông tin đơn hàng.")
                    }
                }

                orderData.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Không có đơn hàng nào cả")
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = "Đơn hàng",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(0.45f))
                        Text(
                            text = "Mô tả",
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
                        if (filteredOrders.isEmpty()){
                            items(orderData) { item ->
                                OrderCard(
                                    order = item!!,
                                    postalCode = item.postalCode,
                                    onShowDetailsClick = {
                                        navController.navigate(Routes.GetOrderByCodeScreen(item.postalCode))
                                    },
                                    onEditClick = {
                                        navController.navigate(Routes.UpdateOrderScreen(item.postalCode))
                                    },
                                    onDeleteClick = {
                                        viewModel.deleteOrder(item.postalCode)
                                        navController.navigate(Routes.GetAllOrderScreen)
                                    }
                                )
                            }
                        }else{
                            items(filteredOrders) { item ->
                                if (item != null) {
                                    OrderCard(
                                        order = item,
                                        postalCode = item.postalCode,
                                        onShowDetailsClick = {
                                            navController.navigate(Routes.GetOrderByCodeScreen(item.postalCode))
                                        },
                                        onEditClick = {
                                            navController.navigate(Routes.UpdateOrderScreen(item.postalCode))
                                        },
                                        onDeleteClick = {
                                            viewModel.deleteOrder(item.postalCode)
                                            navController.navigate(Routes.GetAllOrderScreen)
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

@Composable
fun OrderCard(
    order: OrderDataModels,
    postalCode: String,
    onShowDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formattedPrice = formatCurrencyVND(order.totalPrice)
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Xác nhận xóa")
            },
            text = {
                Text("Bạn có chắc chắn muốn xóa đơn hàng $postalCode này không?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick() // Gọi callback xóa
                    }
                ) {
                    Text("Xóa", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

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
            Image(
                painter = painterResource(id = R.drawable.ecommerce_checkout_laptop_rafiki),
                contentDescription = order.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    text = "Id đ.hàng: ${order.postalCode}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if(order.statusBill == "Đang kiểm duyệt"){
                    Text(
                        text = "Tr.Thái: ${order.statusBill}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.background(color = Color(0xFFFFF3E0)).padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(width = 1.dp, color = Color.Transparent, shape = RoundedCornerShape(5.dp))
                    )
                }else if(order.statusBill == "Đã kiểm duyệt"){
                    Text(
                        text = "Tr.Thái: ${order.statusBill}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.background(color = Color(0xFFE8F5E9)).padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(width = 1.dp, color = Color.Transparent, shape = RoundedCornerShape(5.dp))
                    )
                }
                Text(
                    text = order.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Giá: $formattedPrice đ",
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
                    onClick = onEditClick,
                    backgroundColor = Color(0xFFFFC107),
                    icon = Icons.Default.Edit,
                    contentDescription = "Sửa"
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
}
