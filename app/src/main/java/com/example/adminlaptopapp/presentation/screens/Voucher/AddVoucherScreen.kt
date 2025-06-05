package com.example.adminlaptopapp.presentation.screens.Voucher

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.adminlaptopapp.domain.models.DiscountType
import com.example.adminlaptopapp.domain.models.ShippingDiscountCode
import com.example.adminlaptopapp.presentation.viewModels.VouchersViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddVoucherScreen(
    navController: NavController,
    existingVoucher: ShippingDiscountCode? = null,
    viewModel: VouchersViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var code by remember { mutableStateOf(existingVoucher?.code ?: "") }
    var description by remember { mutableStateOf(existingVoucher?.description ?: "") }
    var discountType by remember { mutableStateOf(existingVoucher?.discountType ?: DiscountType.FIXED_AMOUNT) }
    var discountValue by remember { mutableStateOf(existingVoucher?.discountValue?.toString() ?: "") }
    var maxDiscountAmount by remember { mutableStateOf(existingVoucher?.maxDiscountAmount?.toString() ?: "") }
    var startDate by remember { mutableStateOf(existingVoucher?.startDate ?: System.currentTimeMillis()) }
    var endDate by remember { mutableStateOf(existingVoucher?.endDate ?: System.currentTimeMillis() + 7 * 86400000L) }
    var oncePerCustomer by remember { mutableStateOf(existingVoucher?.appliesOncePerCustomer ?: false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(if (existingVoucher != null) "Sửa mã giảm giá" else "Thêm mã giảm giá") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (code.isBlank() || description.isBlank()) {
                    Toast.makeText(context, "Không được để trống", Toast.LENGTH_SHORT).show()
                    return@FloatingActionButton
                }

                val voucher = ShippingDiscountCode(
                    code = code,
                    description = description,
                    isActive = true,
                    startDate = startDate,
                    endDate = endDate,
                    discountType = discountType,
                    discountValue = discountValue.toDoubleOrNull() ?: 0.0,
                    maxDiscountAmount = maxDiscountAmount.toDoubleOrNull(),
                    appliesOncePerCustomer = oncePerCustomer
                )
                viewModel.addOrUpdateVoucher(voucher)
                navController.popBackStack()
            }) {
                Icon(Icons.Default.Check, contentDescription = "Lưu")
            }
        }
    ) { innerpadding ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(innerpadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ==== Mã & mô tả ====
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Mã code") },
                enabled = existingVoucher == null, // không cho sửa nếu đang ở chế độ chỉnh sửa
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ==== Loại giảm giá ====
            Text("Loại giảm giá", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                    selected = discountType == DiscountType.FIXED_AMOUNT,
                    onClick = { discountType = DiscountType.FIXED_AMOUNT }
                    )
                    Text("Giảm cố định", modifier = Modifier.padding(end = 8.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = discountType == DiscountType.PERCENTAGE,
                        onClick = { discountType = DiscountType.PERCENTAGE }
                    )
                    Text("Giảm %", modifier = Modifier.padding(end = 8.dp))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = discountType == DiscountType.FULL_FREE_SHIP,
                        onClick = { discountType = DiscountType.FULL_FREE_SHIP }
                    )
                    Text("Miễn phí ship")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==== Giá trị giảm ====
            if (discountType != DiscountType.FULL_FREE_SHIP) {
                OutlinedTextField(
                    value = discountValue,
                    onValueChange = { discountValue = it },
                    label = { Text("Giá trị giảm") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (discountType == DiscountType.PERCENTAGE) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = maxDiscountAmount,
                    onValueChange = { maxDiscountAmount = it },
                    label = { Text("Giảm tối đa (nếu có)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            DatePickerField("Ngày bắt đầu", startDate) { startDate = it }
            Spacer(modifier = Modifier.height(8.dp))
            DatePickerField("Ngày kết thúc", endDate) { endDate = it }

            Spacer(modifier = Modifier.height(16.dp))

            // ==== Tùy chọn khác ====
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = oncePerCustomer, onCheckedChange = { oncePerCustomer = it })
                Text("Chỉ dùng 1 lần / khách")
            }
        }
    }
}
@Composable
fun DatePickerField(label: String, timestamp: Long, onDateSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val dateText = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))

    Box(modifier = Modifier.fillMaxWidth().clickable {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                onDateSelected(cal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

