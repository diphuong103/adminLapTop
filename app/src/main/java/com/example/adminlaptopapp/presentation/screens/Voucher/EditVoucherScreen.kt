package com.example.adminlaptopapp.presentation.screens.Voucher

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.adminlaptopapp.presentation.viewModels.VouchersViewModel
import androidx.compose.ui.Modifier


@Composable
fun EditVoucherScreen(
    navController: NavController,
    voucherCode: String,
    viewModel: VouchersViewModel = hiltViewModel()
) {
    val voucherList by viewModel.voucherList.collectAsState()
    val existing = voucherList.find { it.code == voucherCode }

    if (existing != null) {
        AddVoucherScreen(
            navController = navController,
            existingVoucher = existing,
            viewModel = viewModel
        )
    } else {
        Text("Không tìm thấy voucher", modifier = Modifier.padding(16.dp))
    }
}
