package com.example.adminlaptopapp.domain.models

import java.text.SimpleDateFormat
import java.util.Locale.getDefault

data class CategoryDataModels(
    var name: String = "",
    var date: String = SimpleDateFormat("dd/MM/yyyy" , getDefault()).format(System.currentTimeMillis()),
    var createBy:String = "",
    var categoryImage: String = "",

    ) {

}