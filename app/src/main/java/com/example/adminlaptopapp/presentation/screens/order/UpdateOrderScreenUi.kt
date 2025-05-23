package com.example.adminlaptopapp.presentation.screens.order

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.screens.product.formatCurrencyVND
import com.example.adminlaptopapp.presentation.utils.showNotification
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdateOrderScreenUi(
    navController: NavController,
    postalCode: String,
    viewModel: AdminLaptopViewModel = hiltViewModel()
){

    val updateOrderState = viewModel.updateOrderState.collectAsStateWithLifecycle()
    val getOrderByPostalCode = viewModel.getOrderByCodeState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    val nameUp = remember { mutableStateOf("") }
    val totalPriceUp = remember { mutableStateOf("") }
    val postalCodeUp = remember { mutableStateOf("") }
    val quantityUp = remember { mutableStateOf("") }
    val emailUp = remember { mutableStateOf("") }
    val addressUp = remember { mutableStateOf("") }
    val firstNameUp = remember { mutableStateOf("") }
    val lastNameUp = remember { mutableStateOf("") }
    val detail_addressUp = remember { mutableStateOf("") }
    val cityUp = remember { mutableStateOf("") }
    val transportUp = remember { mutableStateOf("") }
    val payUp = remember { mutableStateOf("") }
    val selectedStatus = remember { mutableStateOf("") }

    LaunchedEffect (key1 = Unit){
        viewModel.getOrderByCode(postalCode)
    }

    // Lắng nghe trạng thái
    LaunchedEffect(
        updateOrderState.value.isLoading,
        updateOrderState.value.errorMessage,
        updateOrderState.value.ordersData
    ) {
        if (updateOrderState.value.isLoading) {
            Toast.makeText(context, "Đang cập nhật đơn hàng...", Toast.LENGTH_SHORT).show()
        } else if (updateOrderState.value.errorMessage != null) {
            Toast.makeText(context, updateOrderState.value.errorMessage ?: "Lỗi không xác định", Toast.LENGTH_SHORT).show()
        } else if (updateOrderState.value.ordersData != null) {
            Toast.makeText(context, "Cập nhật đơn hàng thành công", Toast.LENGTH_SHORT).show()


            showNotification(
                context = context,
                title = "Cập nhật đơn hàng",
                message = "Đơn hàng $postalCode của ${getOrderByPostalCode.value.ordersData?.firstName} ${getOrderByPostalCode.value.ordersData?.lastName} đã được cập nhật thành công",
                channelId = "order_channel",
            )

            navController.navigate(Routes.GetAllOrderScreen)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật đơn hàng $postalCode") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        if (updateOrderState.value.isLoading) {
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

            when{
                getOrderByPostalCode.value.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                getOrderByPostalCode.value.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = getOrderByPostalCode.value.errorMessage!!)
                    }
                }
                getOrderByPostalCode.value.ordersData != null -> {
                    val order = getOrderByPostalCode.value.ordersData!!.copy(postalCode = postalCode)

                    LaunchedEffect(order) {
                        nameUp.value = order.name
                        totalPriceUp.value = formatCurrencyVND(order.totalPrice)
                        postalCodeUp.value = order.postalCode
                        quantityUp.value = order.quantity.toString()
                        emailUp.value = order.email
                        addressUp.value = order.address
                        firstNameUp.value = order.firstName
                        lastNameUp.value = order.lastName
                        detail_addressUp.value = order.details_address
                        cityUp.value = order.city
                        transportUp.value = order.transport
                        payUp.value = order.pay
                        selectedStatus.value = order.statusBill
                    }

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

                            InfoRow("Tên đơn hàng", nameUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Mã bưu chính", postalCodeUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Số lượng", quantityUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Email", emailUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Địa chỉ", addressUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Họ tên", "${firstNameUp.value} ${lastNameUp.value}")
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Địa chỉ chi tiết", detail_addressUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Thành phố", cityUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Phương thức vận chuyển", transportUp.value)
                            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                            InfoRow("Phương thức thanh toán", payUp.value)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Giá đơn: ${totalPriceUp.value}",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Cập nhật trạng thái đơn hàng", style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(8.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedStatus.value == "Đang kiểm duyệt",
                                        onClick = { selectedStatus.value = "Đang kiểm duyệt" }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Đang kiểm duyệt")
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = selectedStatus.value == "Đã kiểm duyệt",
                                        onClick = { selectedStatus.value = "Đã kiểm duyệt" }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Đã kiểm duyệt")
                                }
                            }




                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val updateOrder = OrderDataModels(
                                        name = nameUp.value,
                                        quantity = order.quantity,
                                        totalPrice = order.totalPrice,
                                        email = emailUp.value,
                                        address = addressUp.value,
                                        firstName = firstNameUp.value,
                                        lastName = lastNameUp.value,
                                        details_address = detail_addressUp.value,
                                        city = cityUp.value,
                                        postalCode = postalCode,
                                        transport = transportUp.value,
                                        pay = payUp.value,
                                        statusBill = selectedStatus.value
                                    )
                                    viewModel.updateOrder(updateOrder, postalCode)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.orange), // nền cam
                                    contentColor = Color.White // màu chữ/trên icon
                                )

                            ) {
                                Icon(Icons.Default.Update, contentDescription = "Update Order")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Cập nhật trang thái đơn hàng")
                            }
                        }

                    }
                }
            }

        }
    }





}