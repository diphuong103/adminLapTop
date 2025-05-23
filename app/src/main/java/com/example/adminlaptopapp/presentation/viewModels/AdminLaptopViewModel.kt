package com.example.adminlaptopapp.presentation.viewModels

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.common.HomeScreenState
import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.domain.models.UserData
import com.example.adminlaptopapp.domain.models.UserDataParent
import com.example.adminlaptopapp.domain.useCase.AddBannerUseCase
import com.example.adminlaptopapp.domain.useCase.AddCategoryUseCase
import com.example.adminlaptopapp.domain.useCase.AddProductUseCase
import com.example.adminlaptopapp.domain.useCase.CountAllBannerUseCase
import com.example.adminlaptopapp.domain.useCase.CountAllCategoryUseCase
import com.example.adminlaptopapp.domain.useCase.CountAllOrderUseCase
import com.example.adminlaptopapp.domain.useCase.CountAllProducts
import com.example.adminlaptopapp.domain.useCase.DeleteBannerUseCase
import com.example.adminlaptopapp.domain.useCase.DeleteCategoryUseCase
import com.example.adminlaptopapp.domain.useCase.DeleteOrderUseCase
import com.example.adminlaptopapp.domain.useCase.DeleteProductUseCase
import com.example.adminlaptopapp.domain.useCase.DeleteUserUseCase
import com.example.adminlaptopapp.domain.useCase.ExportUserPdfByEmailUseCase
import com.example.adminlaptopapp.domain.useCase.GetAllBannerUseCase
import com.example.adminlaptopapp.domain.useCase.GetAllCategoryUseCase
import com.example.adminlaptopapp.domain.useCase.GetAllOrderByLineChar
import com.example.adminlaptopapp.domain.useCase.GetAllOrderUseCase
import com.example.adminlaptopapp.domain.useCase.GetAllProductUseCase
import com.example.adminlaptopapp.domain.useCase.GetAllUserUseCase
import com.example.adminlaptopapp.domain.useCase.GetBannerByNameUseCase
import com.example.adminlaptopapp.domain.useCase.GetCategoryByNameUseCase
import com.example.adminlaptopapp.domain.useCase.GetOrderByCodeUseCase
import com.example.adminlaptopapp.domain.useCase.GetProductByIdUseCase
import com.example.adminlaptopapp.domain.useCase.GetUserByEmailUseCase
import com.example.adminlaptopapp.domain.useCase.SearchProductUsecase
import com.example.adminlaptopapp.domain.useCase.UpdateBannerUseCase
import com.example.adminlaptopapp.domain.useCase.UpdateCategoryUseCase
import com.example.adminlaptopapp.domain.useCase.UpdateOrderUseCase
import com.example.adminlaptopapp.domain.useCase.UpdateProductUseCase
import com.example.adminlaptopapp.presentation.screens.product.formatCurrencyVND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class AdminLaptopViewModel @Inject constructor(

    private val getAllProductUseCase: GetAllProductUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val searchProductUsecase: SearchProductUsecase,
    private val getAllCategoryUseCase: GetAllCategoryUseCase,
    private val getCategoryByNameUseCase: GetCategoryByNameUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val getAllBannerUseCase: GetAllBannerUseCase,
    private val getBannerByNameUseCase: GetBannerByNameUseCase,
    private val addBannerUseCase: AddBannerUseCase,
    private val deleteBannerUseCase: DeleteBannerUseCase,
    private val updateBannerUseCase: UpdateBannerUseCase,
    private val getAllUserUseCase: GetAllUserUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val exportUserPdfByEmailUseCase: ExportUserPdfByEmailUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getAllOrderUseCase: GetAllOrderUseCase,
    private val getOrderByCodeUseCase: GetOrderByCodeUseCase,
    private val updateOrderUseCase: UpdateOrderUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase,
    private val countAllProductUseCase: CountAllProducts,
    private val countAllCategoryUseCase: CountAllCategoryUseCase,
    private val countAllBannerUseCase: CountAllBannerUseCase,
    private val countAllOrderUseCase: CountAllOrderUseCase,
    private val getAllOrderByLineChar: GetAllOrderByLineChar

    ): ViewModel()
{
    private val _hasNewMessages = MutableStateFlow(false)
    val hasNewMessages: StateFlow<Boolean> = _hasNewMessages.asStateFlow()

    fun updateHasNewMessages(newValue: Boolean) {
        _hasNewMessages.value = newValue
    }

    private val _getAllProductsState = MutableStateFlow(GetAllProductsState())
    val getAllProductsState = _getAllProductsState.asStateFlow()

    private val _getProductByIdState = MutableStateFlow(GetProductByIdState())
    val getProductByIdState = _getProductByIdState.asStateFlow()

    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState = _addProductState.asStateFlow()

    private val _updateProductState = MutableStateFlow(UpdateProductState())
    val updateProductState = _updateProductState.asStateFlow()

    private val _deleteProductState = MutableStateFlow(DeleteProductState())
    val deleteProductState = _deleteProductState.asStateFlow()

    private val _searchProductState = MutableStateFlow(SearchProductState())
    val searchProductState = _searchProductState.asStateFlow()

    private val _getAllCategoryState = MutableStateFlow(GetAllCategoryState())
    val getAllCategoryState = _getAllCategoryState.asStateFlow()

    private val _getCategoryByNameState = MutableStateFlow(GetCategoryByNameState())
    val getCategoryByNameState = _getCategoryByNameState.asStateFlow()

    private val _addCategoryState = MutableStateFlow(AddCategoryState())
    val addCategoryState = _addCategoryState.asStateFlow()

    private val _deleteCategoryState = MutableStateFlow(DeleteCategoryState())
    val deleteCategoryState = _deleteCategoryState.asStateFlow()

    private val _updateCategoryState = MutableStateFlow(UpdateCategoryState())
    val updateCategoryState = _updateCategoryState.asStateFlow()

    private val _getAllBannerState = MutableStateFlow(GetAllBannerState())
    val getAllBannerState = _getAllBannerState.asStateFlow()

    private val _getBannerByNameState = MutableStateFlow(GetBannerByNameState())
    val getBannerByNameState = _getBannerByNameState.asStateFlow()

    private val _addBannerState = MutableStateFlow(AddBannerState())
    val addBannerState = _addBannerState.asStateFlow()

    private val _deleteBannerState = MutableStateFlow(DeleteBannerState())
    val deleteBannerState = _deleteBannerState.asStateFlow()

    private val _updateBannerState = MutableStateFlow(UpdateBannerState())
    val updateBannerState = _updateBannerState.asStateFlow()

    private val _getAllUserState = MutableStateFlow(GetAllUserState())
    val getAllUserState = _getAllUserState.asStateFlow()

    private val _getUserByEmailState = MutableStateFlow(GetUserByEmailState())
    val getUserByEmailState = _getUserByEmailState.asStateFlow()

    private val _exportUserPdfByEmailState = MutableStateFlow(ExportUserPdfByEmailState())
    val exportUserPdfByEmailState = _exportUserPdfByEmailState.asStateFlow()

    private val _deleteUserState = MutableStateFlow(DeleteUserState())
    val deleteUserState = _deleteUserState.asStateFlow()

    private val _getAllOrderState = MutableStateFlow(GetAllOrderState())
    val getAllOrderState = _getAllOrderState.asStateFlow()

    private val _getOrderByCodeState = MutableStateFlow(GetOrderByCodeState())
    val getOrderByCodeState = _getOrderByCodeState.asStateFlow()

    private val _updateOrderState = MutableStateFlow(UpdateOrderState())
    val updateOrderState = _updateOrderState.asStateFlow()

    private val _deleteOrderState = MutableStateFlow(DeleteOrderState())
    val deleteOrderState = _deleteOrderState.asStateFlow()

    private val _homeScreenState = MutableStateFlow(HomeScreenState())
    val homeScreenState = _homeScreenState.asStateFlow()

    fun getAllProducts(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllProductUseCase.getAllProduct().collect{
                when(it){
                    is ResultState.Error ->{
                        _getAllProductsState.value = _getAllProductsState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading ->{
                        _getAllProductsState.value = _getAllProductsState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success ->{
                        _getAllProductsState.value = _getAllProductsState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            productsData = it.data
                        )
                    }
                }
            }
        }
    }
    
//    fun uploadImage(imageUri: Uri){
//        viewModelScope.launch(Dispatchers.IO) {
//            uploadImageUseCase.uploadImage(imageUri).collect {
//                when (it) {
//                    is ResultState.Error -> {
//                        _uploadImageState.value = (_uploadImageState.value.errorMessage?: it.message) as UploadImageState
//                    }
//
//                    is ResultState.Loading -> {
//                        _uploadImageState.value = (_uploadImageState.value.isLoading?: true as UploadImageState) as UploadImageState
//                    }
//
//                    is ResultState.Success -> {
//                        _uploadImageState.value = (_uploadImageState.value.imageUrl?: it.data) as UploadImageState
//                    }
//                }
//            }
//        }
//    }

    fun getProductById(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getProductByIdUseCase.getProductById(productId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getProductByIdState.value = _getProductByIdState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getProductByIdState.value = _getProductByIdState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getProductByIdState.value = _getProductByIdState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            productData = it.data
                        )
                    }
                }
            }
        }
    }

    fun addProduct(product: ProductDataModels) {
        viewModelScope.launch {
            addProductUseCase.addProduct(product).collect {
                    when (it) {
                        is ResultState.Error -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                errorMessage = it.message
                            )
                        }

                        is ResultState.Loading -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = true
                            )
                        }

                        is ResultState.Success -> {
                            _addProductState.value = _addProductState.value.copy(
                                isLoading = false,
                                errorMessage = null,
                                productData = it.data
                            )
                        }
                    }
            }
        }
    }

    fun updateProduct(product: ProductDataModels , productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateProductUseCase.updateproduct(product , productId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _updateProductState.value = _updateProductState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _updateProductState.value = _updateProductState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _updateProductState.value = _updateProductState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            productData = it.data
                            )
                        }
                    }
                }
            }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteProductUseCase.deleteProduct(productId).collect {
                when (it) {
                    is ResultState.Error -> {
                        _deleteProductState.value = _deleteProductState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _deleteProductState.value = _deleteProductState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        getAllProducts()
                        _deleteProductState.value = _deleteProductState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            productData = it.data.toString()
                        )
                    }
                }
            }
        }
    }

    fun searchProduct(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchProductUsecase.searchProduct(query).collect {
                when (it) {
                    is ResultState.Error -> {
                        _searchProductState.value = _searchProductState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _searchProductState.value = _searchProductState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _searchProductState.value = _searchProductState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            productsData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getAllCategory(){
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoryUseCase.getAllCategory().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllCategoryState.value = _getAllCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getAllCategoryState.value = _getAllCategoryState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getAllCategoryState.value = _getAllCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            categoriesData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getCategoryByName(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getCategoryByNameUseCase.getCategoryByName(categoryName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getCategoryByNameState.value = _getCategoryByNameState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getCategoryByNameState.value = _getCategoryByNameState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getCategoryByNameState.value = _getCategoryByNameState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            categoryData = it.data
                        )
                    }
                }
            }
        }
    }

    fun addCategory(category: CategoryDataModels) {
        viewModelScope.launch {
            addCategoryUseCase.addCategory(category).collect {
                when (it) {
                    is ResultState.Error -> {
                        _addCategoryState.value = _addCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _addCategoryState.value = _addCategoryState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        _addCategoryState.value = _addCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            categoryData = it.data
                            )
                    }
                }
            }
        }
    }

    fun deleteCategory(categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCategoryUseCase.deleteCategory(categoryName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _deleteCategoryState.value = _deleteCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }
                    is ResultState.Loading -> {
                        _deleteCategoryState.value = _deleteCategoryState.value.copy(
                            isLoading = true
                        )
                    }
                    is ResultState.Success -> {
                        getAllCategory()
                        _deleteCategoryState.value = _deleteCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            categoryData = it.data.toString()
                        )
                    }
                }
            }
        }
    }

    fun updateCategory(category: CategoryDataModels , categoryName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateCategoryUseCase.updateCategory(category, categoryName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _updateCategoryState.value = _updateCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _updateCategoryState.value = _updateCategoryState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _updateCategoryState.value = _updateCategoryState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            categoryData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getAllBanner() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllBannerUseCase.getAllBanner().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllBannerState.value = _getAllBannerState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getAllBannerState.value = _getAllBannerState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getAllBannerState.value = _getAllBannerState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            bannersData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getBannerByName(bannerName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getBannerByNameUseCase.getBannerByName(bannerName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getBannerByNameState.value = _getBannerByNameState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getBannerByNameState.value = _getBannerByNameState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getBannerByNameState.value = _getBannerByNameState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            bannerData = it.data
                        )
                    }
                }
            }
        }
    }

    fun addBanner(banner: BannerDataModels) {
        viewModelScope.launch {
            addBannerUseCase.addBanner(banner).collect {
                when (it) {
                    is ResultState.Error -> {
                        _addBannerState.value = _addBannerState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _addBannerState.value = _addBannerState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _addBannerState.value = _addBannerState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            bannerData = it.data
                        )
                    }
                }
            }
        }
    }


    fun deleteBanner(bannerName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteBannerUseCase.deleteBanner(bannerName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _deleteBannerState.value = _deleteBannerState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _deleteBannerState.value = _deleteBannerState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        getAllBanner()
                        _deleteBannerState.value = _deleteBannerState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            bannerData = it.data.toString()
                        )
                    }
                }
            }
        }
    }

    fun updateBanner(banner: BannerDataModels , bannerName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateBannerUseCase.updateBanner(banner, bannerName).collect {
                when (it) {
                    is ResultState.Error -> {
                        _updateBannerState.value = _updateBannerState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _updateBannerState.value = _updateBannerState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _updateBannerState.value = _updateBannerState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            bannerData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllUserUseCase.getAllUser().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllUserState.value = _getAllUserState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getAllUserState.value = _getAllUserState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getAllUserState.value = _getAllUserState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            usersData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getUserByEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserByEmailUseCase.getUserByEmail(email).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getUserByEmailState.value = _getUserByEmailState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getUserByEmailState.value = _getUserByEmailState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getUserByEmailState.value = _getUserByEmailState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            userData = it.data
                        )
                    }
                }
            }
        }
    }

//    fun exportUserPdfByEmail(email: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            exportUserPdfByEmailUseCase.exportUserPdfByEmail(email).collect {
//                when (it) {
//                    is ResultState.Error -> {
//                        _exportUserPdfByEmailState.value = _exportUserPdfByEmailState.value.copy(
//                            isLoading = false,
//                            errorMessage = it.message
//                        )
//                    }
//
//                    is ResultState.Loading -> {
//                        _exportUserPdfByEmailState.value = _exportUserPdfByEmailState.value.copy(
//                            isLoading = true
//                        )
//                    }
//
//                    is ResultState.Success -> {
//                        _exportUserPdfByEmailState.value = _exportUserPdfByEmailState.value.copy(
//                            isLoading = false,
//                            errorMessage = null,
//                            userData = it.data
//                        )
//                    }
//                }
//            }
//        }
//    }

//    private suspend fun loadBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
//        return withContext(Dispatchers.IO) {
//            try {
//                Glide.with(context)
//                    .asBitmap()
//                    .load(imageUrl)
//                    .submit()
//                    .get()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }

    private suspend fun loadBitmapFromUrl(context: Context, imageUrl: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val futureTarget = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(
                        RequestOptions()
                            .format(DecodeFormat.PREFER_ARGB_8888) // Đảm bảo chất lượng cao
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache tốt hơn
                            .dontTransform() // Tránh lỗi transform ảnh
                    )
                    .submit()

                futureTarget.get()
            } catch (e: Exception) {
                Log.e("PDF_EXPORT", "Lỗi tải ảnh: ${e.message}", e)
                null
            }
        }
    }



    @SuppressLint("NewApi")
    suspend fun exportUserToPdf(context: Context, user: UserData) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // Vẽ nền cam
        canvas.drawColor(Color.parseColor("#FFFF9800"))

        // Tải ảnh từ URL (imgbb)
        val logoBitmap = loadBitmapFromUrl(context, user.profileImage ?: "")

        if (logoBitmap != null) {
            val size = 140
            val centerX = (pageInfo.pageWidth - size) / 2f
            val topY = 40f
            val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, size, size, true)

            // Vẽ viền trắng bo tròn
            val path = Path().apply {
                addCircle(centerX + size / 2, topY + size / 2, size / 2f, Path.Direction.CW)
            }
            canvas.save()
            canvas.clipPath(path)
            canvas.drawBitmap(scaledLogo, centerX, topY, null)
            canvas.restore()

            // Viền trắng ngoài
            paint.style = Paint.Style.STROKE
            paint.color = Color.WHITE
            paint.strokeWidth = 6f
            canvas.drawCircle(centerX + size / 2, topY + size / 2, size / 2f, paint)
            paint.style = Paint.Style.FILL
        } else {
            paint.color = Color.WHITE
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 18f
            canvas.drawText("Không thể tải ảnh đại diện", pageInfo.pageWidth / 2f, 100f, paint)
        }

        // Tiêu đề
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("LapTop Store", pageInfo.pageWidth / 2f, 200f, paint)

        // Nền trắng cho nội dung
        val contentTop = 240
        val contentBottom = pageInfo.pageHeight
        canvas.drawRect(
            0f, contentTop.toFloat(),
            pageInfo.pageWidth.toFloat(), contentBottom.toFloat(),
            Paint().apply { color = Color.WHITE }
        )

        // Nội dung bảng
        paint.textAlign = Paint.Align.LEFT
        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        val padding = 40
        val labelWidth = 130f
        val lineSpacing = 36
        var y = contentTop + 50

        fun drawRow(label: String, value: String?) {
            val displayValue = if (value.isNullOrBlank()) "Chưa điền" else value
            canvas.drawText("$label:", padding.toFloat(), y.toFloat(), paint)
            canvas.drawText(displayValue, padding + labelWidth, y.toFloat(), paint)
            y += lineSpacing
        }

        canvas.drawLine(padding.toFloat(), y.toFloat() + 10, pageInfo.pageWidth - padding.toFloat(), y.toFloat() + 10, paint)
        drawRow("Họ", user.firstName)
        canvas.drawLine(padding.toFloat(), y.toFloat() + 10, pageInfo.pageWidth - padding.toFloat(), y.toFloat() + 10, paint)
        drawRow("Tên", user.lastName)
        canvas.drawLine(padding.toFloat(), y.toFloat() + 10, pageInfo.pageWidth - padding.toFloat(), y.toFloat() + 10, paint)
        drawRow("Họ và tên", "${user.firstName ?: ""} ${user.lastName ?: ""}".trim().ifBlank { "Chưa điền" })
        canvas.drawLine(padding.toFloat(), y.toFloat() + 10, pageInfo.pageWidth - padding.toFloat(), y.toFloat() + 10, paint)
        drawRow("Email", user.email)
        canvas.drawLine(padding.toFloat(), y.toFloat() + 10, pageInfo.pageWidth - padding.toFloat(), y.toFloat() + 10, paint)
        drawRow("Số điện thoại", user.phoneNumber)
        canvas.drawLine(padding.toFloat(), y.toFloat() + 10, pageInfo.pageWidth - padding.toFloat(), y.toFloat() + 10, paint)
        drawRow("Địa chỉ", user.address)

        pdfDocument.finishPage(page)

        val fileName = "${user.firstName ?: "User"}_${user.lastName ?: ""}_${System.currentTimeMillis()}.pdf"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(context, "Đã lưu PDF vào Tải xuống!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi lưu PDF: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                pdfDocument.close()
            }
        } else {
            Toast.makeText(context, "Không thể tạo file PDF!", Toast.LENGTH_LONG).show()
            pdfDocument.close()
        }
    }

    fun deleteUser(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteUserUseCase.deleteUser(email).collect {
                when (it) {
                    is ResultState.Error -> {
                        _deleteUserState.value = _deleteUserState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _deleteUserState.value = _deleteUserState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        getAllUser()
                        _deleteUserState.value = _deleteUserState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            userData = it.data.toString()
                        )
                    }
                }
            }
        }
    }

    fun getAllOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllOrderUseCase.getAllOrder().collect {
                when (it) {
                    is ResultState.Error -> {
                        _getAllOrderState.value = _getAllOrderState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getAllOrderState.value = _getAllOrderState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getAllOrderState.value = _getAllOrderState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            ordersData = it.data
                        )
                    }
                }
            }
        }
    }

    fun getOrderByCode(orderCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getOrderByCodeUseCase.getOrderByCode(orderCode).collect {
                when (it) {
                    is ResultState.Error -> {
                        _getOrderByCodeState.value = _getOrderByCodeState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _getOrderByCodeState.value = _getOrderByCodeState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _getOrderByCodeState.value = _getOrderByCodeState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            ordersData = it.data
                        )
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    suspend fun exportOrderToPdf(context: Context, order: OrderDataModels) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint()
        val titlePaint = Paint()
        val boldPaint = Paint()

        // Background color
        canvas.drawColor(Color.parseColor("#FFFF9800"))

        // Normal text
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.isAntiAlias = true

        // Title
        titlePaint.color = Color.BLACK
        titlePaint.textSize = 18f
        titlePaint.isFakeBoldText = true

        // Bold text (for price)
        boldPaint.color = Color.BLACK
        boldPaint.textSize = 18f
        boldPaint.isFakeBoldText = true

        val startX = 40f
        var startY = 60f
        val lineSpacing = 30f
        val pageWidth = pageInfo.pageWidth
        val pageHeight = pageInfo.pageHeight
        val padding = 40f

        // Image
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ecommerce_checkout_laptop_rafiki)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 200, true)
        canvas.drawBitmap(scaledBitmap, (pageWidth - 300) / 2f, startY, null)
        startY += 220f

        // Title
        canvas.drawText("CHI TIẾT ĐƠN HÀNG", startX, startY, titlePaint)
        startY += 40f

        // Function to draw row and line
        fun drawRowWithLine(label: String, value: String) {
            canvas.drawText("$label:", startX, startY, paint)
            canvas.drawText(if (value.isNullOrBlank()) "Chưa điền" else value, startX + 200f, startY, paint)
            startY += lineSpacing
            canvas.drawLine(padding, startY - 50f, pageWidth - padding, startY - 50f, paint)
        }

        // Order details
        drawRowWithLine("Tên đơn hàng", order.name)
        drawRowWithLine("Mã bưu chính", order.postalCode)
        drawRowWithLine("Số lượng", order.quantity.toString())
        drawRowWithLine("Email", order.email)
        drawRowWithLine("Địa chỉ", order.address)
        drawRowWithLine("Họ tên", "${order.firstName} ${order.lastName}")
        drawRowWithLine("Địa chỉ chi tiết", order.details_address)
        drawRowWithLine("Thành phố", order.city)
        drawRowWithLine("Phương thức vận chuyển", order.transport)
        drawRowWithLine("Phương thức thanh toán", order.pay)
        drawRowWithLine("Trạng thái đơn hàng", order.statusBill)
        drawRowWithLine("Ngày đặt hàng", order.date)

        // Giá đơn - bottom end (right-bottom corner)
//        val priceText = String.format("Giá đơn: %.0f VNĐ", order.totalPrice)
        val priceText = String.format("Giá đơn: ${formatCurrencyVND(order.totalPrice)} VNĐ")
        val textWidth = boldPaint.measureText(priceText)
        val priceY = pageHeight - 40f // 40 px margin from bottom
        canvas.drawText(priceText, pageWidth - padding - textWidth, priceY, boldPaint)

        pdfDocument.finishPage(page)

        // Save to Downloads
        val fileName = "order_${order.firstName}_${order.lastName}_${System.currentTimeMillis()}.pdf"
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val contentResolver = context.contentResolver
        val pdfUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (pdfUri != null) {
            try {
                contentResolver.openOutputStream(pdfUri)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    Toast.makeText(context, "Đã lưu vào Tải xuống: $fileName", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi lưu PDF: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                pdfDocument.close()
            }
        } else {
            Toast.makeText(context, "Không thể tạo file PDF!", Toast.LENGTH_LONG).show()
            pdfDocument.close()
        }
    }


    fun updateOrder(order: OrderDataModels , orderCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateOrderUseCase.updateOrder(order, orderCode).collect {
                when (it) {
                    is ResultState.Error -> {
                        _updateOrderState.value = _updateOrderState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _updateOrderState.value = _updateOrderState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        _updateOrderState.value = _updateOrderState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            ordersData = it.data
                        )
                    }
                }
            }
        }
    }

    fun deleteOrder(orderCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteOrderUseCase.deleteOrder(orderCode).collect {
                when (it) {
                    is ResultState.Error -> {
                        _deleteOrderState.value = _deleteOrderState.value.copy(
                            isLoading = false,
                            errorMessage = it.message
                        )
                    }

                    is ResultState.Loading -> {
                        _deleteOrderState.value = _deleteOrderState.value.copy(
                            isLoading = true
                        )
                    }

                    is ResultState.Success -> {
                        getAllOrder()
                        _deleteOrderState.value = _deleteOrderState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            ordersData = it.data.toString()
                        )
                    }
                }
            }
        }
    }

    init {
        loadHomeScreenData()
    }

    private fun loadHomeScreenData() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                countAllProductUseCase.countAllProducts(),
                countAllCategoryUseCase.countAllCategory(),
                countAllBannerUseCase.countAllBanner(),
                countAllOrderUseCase.countAllOrder(),
                getAllOrderUseCase.getAllOrder(),
                getAllOrderByLineChar.getAllOrderByLineChar()
            ) { results: Array<Any?> ->
                val productsResult = results[0] as ResultState<String>
                val categoriesResult = results[1] as ResultState<String>
                val bannersResult = results[2] as ResultState<String>
                val ordersResult = results[3] as ResultState<String>
                val ordersListResult = results[4] as ResultState<List<OrderDataModels>>
                val chartOrdersResult = results[5] as ResultState<List<OrderDataModels>>

                when {
                    listOf(
                        productsResult,
                        categoriesResult,
                        bannersResult,
                        ordersResult,
                        ordersListResult,
                        chartOrdersResult
                    ).any { it is ResultState.Error } -> {
                        val firstError = listOf(
                            productsResult,
                            categoriesResult,
                            bannersResult,
                            ordersResult,
                            ordersListResult,
                            chartOrdersResult
                        ).first { it is ResultState.Error } as ResultState.Error
                        HomeScreenState(
                            isLoading = false,
                            errorMessage = firstError.message
                        )
                    }

                    listOf(
                        productsResult,
                        categoriesResult,
                        bannersResult,
                        ordersResult,
                        ordersListResult,
                        chartOrdersResult
                    ).any { it is ResultState.Loading } -> {
                        HomeScreenState(
                            isLoading = true
                        )
                    }

                    productsResult is ResultState.Success &&
                            categoriesResult is ResultState.Success &&
                            bannersResult is ResultState.Success &&
                            ordersResult is ResultState.Success &&
                            ordersListResult is ResultState.Success &&
                            chartOrdersResult is ResultState.Success -> {

                        val totalRevenueValue = ordersListResult.data.sumOf { it.totalPrice }

                        HomeScreenState(
                            isLoading = false,
                            errorMessage = null,
                            categories = categoriesResult.data,
                            products = productsResult.data,
                            banners = bannersResult.data,
                            orders = ordersResult.data,
                            totalRevenue = totalRevenueValue.toString(),
                            lineChartOrders = chartOrdersResult.data
                        )
                    }

                    else -> HomeScreenState(
                        isLoading = true,
                        errorMessage = "Unknown error"
                    )
                }
            }.collect { state ->
                _homeScreenState.value = state
            }
        }
    }






}

class UploadImageState {
    var isLoading: Boolean = false
    var errorMessage: String? = null
    var imageUrl: String? = null
}

data class GetAllProductsState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val productsData: List<ProductDataModels?> = emptyList(),
)



data class GetProductByIdState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productData : ProductDataModels? = null
)

data class AddProductState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productData: String? = null
)


data class UpdateProductState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productData : String? = null
)

data class DeleteProductState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productData : String? = null
)

data class SearchProductState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productsData : List<ProductDataModels?> = emptyList()
)

data class GetAllCategoryState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val categoriesData: List<CategoryDataModels?> = emptyList(),
)

data class GetCategoryByNameState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categoryData : CategoryDataModels? = null
)

data class AddCategoryState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categoryData: String? = null
)

data class DeleteCategoryState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categoryData : String? = null
)

data class UpdateCategoryState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categoryData : String? = null
)

data class GetAllBannerState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val bannersData: List<BannerDataModels?> = emptyList(),
)

data class GetBannerByNameState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bannerData : BannerDataModels? = null
)

data class AddBannerState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bannerData: String? = null
)

data class DeleteBannerState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bannerData : String? = null
)

data class UpdateBannerState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bannerData : String? = null
)

data class GetAllUserState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val usersData: List<UserDataParent?> = emptyList(),
)

data class GetUserByEmailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData : UserDataParent? = null
)

data class ExportUserPdfByEmailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData : UserDataParent? = null
)
data class DeleteUserState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userData : String? = null
)

data class GetAllOrderState(
    val isLoading : Boolean = false,
    val errorMessage : String? = null,
    val ordersData: List<OrderDataModels?> = emptyList(),
)

data class GetOrderByCodeState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ordersData : OrderDataModels? = null
)

data class UpdateOrderState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ordersData : String? = null
)

data class DeleteOrderState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null ,
    val ordersData : String? = null
)

data class CountAllProductState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val productData :String? = null
)

