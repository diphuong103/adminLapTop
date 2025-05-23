package com.example.adminlaptopapp.presentation.navigations

import kotlinx.serialization.Serializable

sealed class SubNavigation{
    @Serializable
    object MainHomeScreen : SubNavigation()

    @Serializable
    object StartAppScreen : SubNavigation()
}


sealed class Routes{
    @Serializable
    object HomeScreen

    @Serializable
    object StartAppScreen

    //=================================

    @Serializable
    object GetAllProductScreen

    @Serializable
    data class GetProductByIdScreen(val productId: String)

    @Serializable
    object AddProductScreen

    @Serializable
    data class UpdateProductScreen(val productId: String)

    //=================================

    @Serializable
    object GetAllCategoryScreen

    @Serializable
    data class GetCategoryByNameScreen(val categoryName: String)

    @Serializable
    object AddCategoryScreen

    @Serializable
    data class UpdateCategoryScreen(val categoryName: String)

    //=================================

    @Serializable
    object GetAllBannerScreen

    @Serializable
    data class GetBannerByNameScreen(val bannerName: String)

    @Serializable
    object AddBannerScreen

    @Serializable
    data class UpdateBannerScreen(val bannerName: String)

    //=================================

    @Serializable
    object GetAllUserScreen

    @Serializable
    data class GetUserByEmailScreen(val userEmail: String)

    @Serializable
    data class ExportUserPdfByEmailScreen(val userEmail: String)

    //=================================

    @Serializable
    object GetAllOrderScreen

    @Serializable
    data class GetOrderByCodeScreen(val postalCode: String)

    @Serializable
    data class UpdateOrderScreen(val postalCode: String)

//===============Voucher======================

    @Serializable
    object AddVoucherScreen

    @Serializable
    object VoucherScreen


    @Serializable
    data class EditVoucherScreen(val code: String)
    //////////////Chat
    @Serializable
    object ChatListScreen

    @Serializable
    data class ChatScreen(val userId: String, val chatRoomId: String)

}