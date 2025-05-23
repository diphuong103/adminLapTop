package com.example.adminlaptopapp.domain.models

import java.text.SimpleDateFormat
import java.util.Locale.getDefault

data class BannerDataModels(
    var name : String = "",
    var image : String = "",
    var date : String = SimpleDateFormat("dd/MM/yyyy" , getDefault()).format(System.currentTimeMillis())
)