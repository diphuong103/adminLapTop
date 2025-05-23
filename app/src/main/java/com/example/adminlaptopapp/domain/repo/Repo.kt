package com.example.adminlaptopapp.domain.repo

import android.net.Uri
import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.domain.models.UserDataParent
import kotlinx.coroutines.flow.Flow

interface Repo {

//    fun uploadImage(imageUri: Uri): Flow<ResultState<String>>

    fun getAllProduct() : Flow<ResultState<List<ProductDataModels>>>
    fun getProductById(productId: String): Flow<ResultState<ProductDataModels>>
    fun addProduct(product: ProductDataModels): Flow<ResultState<String>>
    fun deleteProduct(productId: String): Flow<ResultState<ProductDataModels>>
    fun updateProduct(product: ProductDataModels , productId: String): Flow<ResultState<String>>
    fun searchProducts(query: String): Flow<ResultState<List<ProductDataModels>>>

    fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>>
    fun getCategoryByName(categoryName: String): Flow<ResultState<CategoryDataModels>>
    fun addCategory(category: CategoryDataModels): Flow<ResultState<String>>
    fun deleteCategory(categoryName: String): Flow<ResultState<CategoryDataModels>>
    fun updateCategory(category: CategoryDataModels , categoryName: String): Flow<ResultState<String>>

    fun getAllBanners(): Flow<ResultState<List<BannerDataModels>>>
    fun getBannerByName(bannerName: String): Flow<ResultState<BannerDataModels>>
    fun addBanner(banner: BannerDataModels): Flow<ResultState<String>>
    fun deleteBanner(bannerName: String): Flow<ResultState<BannerDataModels>>
    fun updateBanner(banner: BannerDataModels , bannerName: String): Flow<ResultState<String>>

    fun getAllUsers(): Flow<ResultState<List<UserDataParent>>>
    fun getUserByEmail(email : String): Flow<ResultState<UserDataParent>>
    // hướng dẫn viết hàm in file pdf lấy dữ liệu người dùng theo email và download file pdf đó về máy
    fun exportUserPdfByEmail(email: String): Flow<ResultState<UserDataParent>>
    fun deleteUser(email: String): Flow<ResultState<UserDataParent>>

    fun getAllOrders(): Flow<ResultState<List<OrderDataModels>>>
    fun getOrderByCode(postalCode: String): Flow<ResultState<OrderDataModels>>
    fun updateOrder(order: OrderDataModels , postalCode: String): Flow<ResultState<String>>
    fun deleteOrder(postalCode: String): Flow<ResultState<OrderDataModels>>

    fun countAllProducts(): Flow<ResultState<String>>
    fun countAllCategories(): Flow<ResultState<String>>
    fun countAllBanners(): Flow<ResultState<String>>
    fun countAllOrders(): Flow<ResultState<String>>
    fun getAllOrderByLineChar():Flow<ResultState<List<OrderDataModels>>>


}