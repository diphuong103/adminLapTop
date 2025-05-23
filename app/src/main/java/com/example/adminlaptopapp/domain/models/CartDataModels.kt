package com.example.adminlaptopapp.domain.models

data class CartDataModels(
    var productId: String = "",
    var name: String = "",
    var image: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    var totalPrice: Double = 0.0,
    var cartId : String = "",
    var target: String = "",
    var short_desc: String = "",
    var long_desc: String = "",
    var category: String = "",
)