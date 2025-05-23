package com.example.adminlaptopapp.presentation.screens.Voucher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.adminlaptopapp.domain.models.DiscountType
import com.example.adminlaptopapp.domain.models.ShippingDiscountCode
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.viewModels.VouchersViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingVoucherScreen(
    navController: NavController,
    viewModel: VouchersViewModel = hiltViewModel()
) {
    val vouchers = viewModel.voucherList.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Vouchers", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.AddVoucherScreen) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm mã")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vouchers) { voucher ->
                ShippingVoucherItem(
                    voucher = voucher,
                    onEdit = { navController.navigate(Routes.EditVoucherScreen(code = voucher.code)) },
                    onDelete = { viewModel.deleteVoucher(voucher.code) }
                )
            }
        }
    }
}

@Composable
fun ShippingVoucherItem(
    voucher: ShippingDiscountCode,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = voucher.code,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Sửa",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xoá",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Text(
                text = voucher.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (voucher.discountType) {
                    DiscountType.FIXED_AMOUNT -> "Giảm ${voucher.discountValue.toInt()}đ"
                    DiscountType.PERCENTAGE -> "Giảm ${voucher.discountValue.toInt()}%, tối đa ${voucher.maxDiscountAmount?.toInt() ?: "không giới hạn"}đ"
                    DiscountType.FULL_FREE_SHIP -> "Miễn phí ship hoàn toàn"
                },
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Hiệu lực: ${formatDate(voucher.startDate)} - ${formatDate(voucher.endDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = if (voucher.appliesOncePerCustomer) "Chỉ dùng 1 lần / khách" else "Dùng nhiều lần",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

fun formatDate(epoch: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(epoch))
}
