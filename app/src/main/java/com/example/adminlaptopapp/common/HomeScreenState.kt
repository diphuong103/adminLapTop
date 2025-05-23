package com.example.adminlaptopapp.common

import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.models.ProductDataModels

//data class HomeScreenState(
//    val isLoading: Boolean = true,
//    val errorMessage: String? = null,
//    val categories: List<CategoryDataModels>? = null,
//    val products: List<ProductDataModels>? = null,
//    val banners: List<BannerDataModels>? = null,
//    val orders: List<OrderDataModels>? = null
//) {
//
//
//}

data class HomeScreenState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val categories: String? = null,
    val products: String? = null,
    val banners: String? = null,
    val orders: String? = null,
    val totalRevenue: String? = null,
    val lineChartOrders : List<OrderDataModels>? = emptyList()
) {


}