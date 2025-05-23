package com.example.adminlaptopapp.presentation.screens.order

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.presentation.screens.product.formatCurrencyVND
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GetOrderByCodeScreenUi(
    navController: NavController,
    postalCode : String,
    viewModel: AdminLaptopViewModel = hiltViewModel()
){

    val getOrderByCode = viewModel.getOrderByCodeState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.getOrderByCode(postalCode)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text("Chi tiết Đơn hàng")
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

        when{
            getOrderByCode.value.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            getOrderByCode.value.errorMessage != null -> {
                Text(text = getOrderByCode.value.errorMessage!!)
            }

            getOrderByCode.value.ordersData != null -> {
                val order = getOrderByCode.value.ordersData!!.copy(postalCode = postalCode)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ){
                    Box(
                        modifier = Modifier.height(200.dp).align(Alignment.CenterHorizontally)
                    ){
                        Image(
                            painter = painterResource(R.drawable.ecommerce_checkout_laptop_rafiki),
                            contentDescription = null
                        )

                    }
                    Column(modifier = Modifier.padding(20.dp)) {

                        val labelStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        val valueStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal)

                        @Composable
                        fun InfoRow(label: String, value: String) {
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "$label:",
                                    modifier = Modifier.weight(1f),
                                    style = labelStyle
                                )
                                Text(
                                    text = value,
                                    modifier = Modifier.weight(1f),
                                    style = valueStyle
                                )
                            }
                        }

                        InfoRow("Tên đơn hàng", order.name)
                        InfoRow("Mã bưu chính", order.postalCode)
                        InfoRow("Số lượng", order.quantity.toString())
                        InfoRow("Email", order.email)
                        InfoRow("Địa chỉ", order.address)
                        InfoRow("Họ tên", "${order.firstName} ${order.lastName}")
                        InfoRow("Địa chỉ chi tiết", order.details_address)
                        InfoRow("Thành phố", order.city)
                        InfoRow("Phương thức vận chuyển", order.transport)
                        InfoRow("Phương thức thanh toán", order.pay)
                        InfoRow("Trạng thái đơn hàng", order.statusBill)
                        InfoRow("Ngày đặt hàng", order.date)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Giá đơn: ${formatCurrencyVND(order.totalPrice)}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.exportOrderToPdf(context, order)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export PDF")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Xuất PDF Đơn hàng")
                        }
                    }

                }
            }
        }

    }

}