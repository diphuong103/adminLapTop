package com.example.adminlaptopapp.domain.models

import java.text.SimpleDateFormat
import java.util.Locale.getDefault

data class OrderDataModels(
    var name : String = "",
    var productId : String = "",
    var userId : String = "",
    var quantity : Int = 0,
    var totalPrice : Double = 0.0,
    var email : String = "",
    var address : String = "",
    var firstName : String = "",
    var lastName : String = "",
    var details_address : String = "",
    var city : String = "",
    var postalCode : String = "",
    var transport : String = "",
    var pay : String = "",
    var statusBill : String = "Đang kiểm duyệt",
    var date : String = SimpleDateFormat("dd/MM/yyyy" , getDefault()).format(System.currentTimeMillis())
)