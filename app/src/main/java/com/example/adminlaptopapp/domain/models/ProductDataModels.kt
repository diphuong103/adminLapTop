package com.example.adminlaptopapp.domain.models

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

//@Serializable
data class ProductDataModels(
    var productId: String = "",
    var name : String = "",
    var price : Double = 0.0,
    var short_desc: String = "",
    var long_desc: String = "",
    var quantity : Int = 0,
    var target : String = "",
    var category : String = "",
    var status : String = "",
    var image : String = "",
    var date : String = SimpleDateFormat("dd/MM/yyyy" , getDefault()).format(System.currentTimeMillis()),
    var createBy : String = ""
)