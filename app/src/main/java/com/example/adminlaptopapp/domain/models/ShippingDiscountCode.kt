package com.example.adminlaptopapp.domain.models

data class ShippingDiscountCode(
    val code: String = "",
    val description: String = "",
    val isActive: Boolean = true,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val discountType: DiscountType = DiscountType.FIXED_AMOUNT,
    val discountValue: Double = 0.0,
    val maxDiscountAmount: Double? = null,
    val appliesOncePerCustomer: Boolean = false
)

enum class DiscountType {
    FIXED_AMOUNT,
    PERCENTAGE,
    FULL_FREE_SHIP
}
