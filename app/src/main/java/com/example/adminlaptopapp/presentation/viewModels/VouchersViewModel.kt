package com.example.adminlaptopapp.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.example.adminlaptopapp.domain.models.ShippingDiscountCode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor() : ViewModel() {
    private val db = FirebaseDatabase.getInstance().getReference("vouchers")
    private val _voucherList = MutableStateFlow<List<ShippingDiscountCode>>(emptyList())
    val voucherList: StateFlow<List<ShippingDiscountCode>> = _voucherList

    init {
        loadVouchers()
    }

    private fun loadVouchers() {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(ShippingDiscountCode::class.java) }
                _voucherList.value = list
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun addOrUpdateVoucher(voucher: ShippingDiscountCode) {
        db.child(voucher.code).setValue(voucher)
    }

    fun deleteVoucher(code: String) {
        db.child(code).removeValue()
    }
}
