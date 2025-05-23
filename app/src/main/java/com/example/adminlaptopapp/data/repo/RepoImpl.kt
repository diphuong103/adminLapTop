package com.example.adminlaptopapp.data.repo

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.adminlaptopapp.common.ORDER
import com.example.adminlaptopapp.common.PRODUCTS_COLLECTION
import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.common.USER_COLLECTION
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.domain.models.UserData
import com.example.adminlaptopapp.domain.models.UserDataParent
import com.example.adminlaptopapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.InputStream
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private var firebaseAuth: FirebaseAuth,
    private var firebaseFirestore: FirebaseFirestore,
    private var context: Context
): Repo {
    private val IMG_BB_API_KEY = "cfb88fd6087fa222b489b186dff8c38d"

    // Hàm tải ảnh lên ImgBB
    private suspend fun uploadImageToImgBB(fileUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
                val byteArray = inputStream?.readBytes()
                inputStream?.close()

                if (byteArray == null) throw Exception("Không thể đọc file")

                val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

                val requestBody = FormBody.Builder()
                    .add("key", IMG_BB_API_KEY)
                    .add("image", encodedImage)
                    .build()

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(requestBody)
                    .build()

                val client = OkHttpClient()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful){
                    throw Exception("Upload thất bại")
                }

                val responseBody = response.body?.string()
                val json = JSONObject(responseBody ?: "")
                json.getJSONObject("data").getString("url")
            } catch (e: Exception) {
                null
            }
        }
    }

//    override fun uploadImage(imageUri: Uri): Flow<ResultState<String>> = callbackFlow{
//        trySend(ResultState.Loading)
//        val imageUrl = uploadImageToImgBB(imageUri)
//
//        if (imageUrl != null) {
//            trySend(ResultState.Success(imageUrl))
//        } else {
//            trySend(ResultState.Error("Không thể tải ảnh lên"))
//        }
//
//        awaitClose{
//            close()
//        }
//    }

    override fun getAllProduct(): Flow<ResultState<List<ProductDataModels>>> = callbackFlow{
        trySend(ResultState.Loading)
        // lấy tất cả dữ liệu từ name , price , short_desc , long_desc , quantity , target , category , image , date , createBy của product từ firebasestore
        firebaseFirestore.collection(PRODUCTS_COLLECTION).get().addOnSuccessListener{
                querySnapshot->
            val products = querySnapshot.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {
                    productId = document.id
                }
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
        }
        awaitClose{
            close()
        }
    }

    override fun getProductById(productId: String): Flow<ResultState<ProductDataModels>> = callbackFlow{
        trySend(ResultState.Loading).isSuccess

        Log.d("FirestoreDebug", "Getting product with ID: $productId")

        firebaseFirestore.collection(PRODUCTS_COLLECTION)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(ProductDataModels::class.java)

                if (product != null) {
                    product.productId = document.id // Nếu bạn cần lưu lại id
                    Log.d("FirestoreDebug", "Successfully fetched product: ${product.name}")
                    trySend(ResultState.Success(product)).isSuccess
                } else {
                    Log.e("FirestoreError", "Product not found for ID: $productId")
                    trySend(ResultState.Error("Product not found")).isSuccess
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching product by ID: ${exception.message}", exception)
                trySend(ResultState.Error(exception.message ?: "Unknown error")).isSuccess
            }

        awaitClose {
            close()
        }
    }

    override fun addProduct(product: ProductDataModels): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // thực hiện thêm sản phẩm
        val imageUrl = uploadImageToImgBB(Uri.parse(product.image))
        if (imageUrl != null) {
            product.image = imageUrl
            firebaseFirestore.collection(PRODUCTS_COLLECTION).add(product).addOnSuccessListener {
                trySend(ResultState.Success("Thêm sản phẩm thành công"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
                Log.e("FirestoreError", "Error adding product: ${it.message}", it)
            }
            Log.d("FirestoreDebug", "Successfully added product: ${product.name}")
        } else {
            trySend(ResultState.Error("Upload ảnh thất bại"))
        }
        awaitClose{
            close()
        }
    }

    override fun updateProduct(product: ProductDataModels , productId: String): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // thực hiện cập nhật sản phẩm cũng như tải ảnh lên imgbb
        val imageUrl = uploadImageToImgBB(Uri.parse(product.image))

        if (imageUrl != null) {
            product.image = imageUrl
            firebaseFirestore.collection(PRODUCTS_COLLECTION).document(productId).set(product).addOnSuccessListener {
                trySend(ResultState.Success("Cập nhật sản phẩm thành công"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
                Log.e("FirestoreError", "Error updating product: ${it.message}", it)
            }
            Log.d("FirestoreDebug", "Successfully updated product: ${product.name}")
        } else {
            trySend(ResultState.Error("Upload ảnh thất bại"))
        }
        awaitClose{
            close()
        }
    }

    override fun deleteProduct(productId: String): Flow<ResultState<ProductDataModels>> = callbackFlow{
        trySend(ResultState.Loading)
        // thực hiện xóa sản phẩm
        firebaseFirestore.collection(PRODUCTS_COLLECTION).document(productId).delete().addOnSuccessListener {
            trySend(ResultState.Success(ProductDataModels()))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message ?: "Unknown error"))
            Log.e("FirestoreError",
                "Error deleting product: ${it.message}", it)
        }
        awaitClose {
            close()
        }
    }

    override fun searchProducts(query: String): Flow<ResultState<List<ProductDataModels>>> = callbackFlow{
        trySend(ResultState.Loading)
        // thực hiện tìm kiếm sản phẩm theo tên sản phẩm hiện dữ liệu ngay lập tức như ajax trong web
        firebaseFirestore.collection(PRODUCTS_COLLECTION)
            .whereGreaterThanOrEqualTo("name", query)
//            .whereGreaterThanOrEqualTo("price" , query)
            .whereGreaterThanOrEqualTo("quantity" , query)
            .whereGreaterThanOrEqualTo("category" , query)
            .get().addOnSuccessListener {
                querySnapshot ->
            val products = querySnapshot.documents.mapNotNull { document ->
                document.toObject(ProductDataModels::class.java)?.apply {
                    productId = document.id
                    Log.d("FirestoreDebug", "Successfully fetched product: ${this.price}")
                }
            }
            trySend(ResultState.Success(products))
        }.addOnFailureListener {
            trySend(ResultState.Error(it.message ?: "Unknown error"))
            Log.e("FirestoreError", "Error searching products: ${it.message}", it)
        }
        awaitClose {
            close()
        }
    }

    override fun getAllCategories(): Flow<ResultState<List<CategoryDataModels>>> = callbackFlow{
        trySend(ResultState.Loading)
        // lấy tất cả dữ liệu từ name , image của category từ firebasestore
        firebaseFirestore.collection("categories").get().addOnSuccessListener{
                querySnapshot->
            val categories = querySnapshot.documents.mapNotNull { document ->
                document.toObject(CategoryDataModels::class.java)
            }
            trySend(ResultState.Success(categories))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
        }
        awaitClose{
            close()
        }
    }

    override fun getCategoryByName(categoryName: String): Flow<ResultState<CategoryDataModels>> = callbackFlow {
        trySend(ResultState.Loading)

        Log.d("FirestoreDebug", "Searching category with name field: $categoryName")

        firebaseFirestore.collection("categories")
            .whereEqualTo("name", categoryName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val category = document.toObject(CategoryDataModels::class.java)
                    if (category != null) {
                        Log.d("FirestoreDebug", "Found category: ${category.name}")
                        trySend(ResultState.Success(category))
                    } else {
                        Log.e("FirestoreError", "Category data is null even though document exists")
                        trySend(ResultState.Error("Data parsing failed"))
                    }
                } else {
                    Log.e("FirestoreError", "No category found with name: $categoryName")
                    trySend(ResultState.Error("Category not found"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Failed to fetch category: ${exception.message}")
                trySend(ResultState.Error(exception.message ?: "Unknown error"))
            }

        awaitClose { close() }
    }


    override fun addCategory(category: CategoryDataModels): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        // thực hiện thêm name , image của category vào firebasestore
        val imageUrl = uploadImageToImgBB(Uri.parse(category.categoryImage))
        if (imageUrl != null) {
            category.categoryImage = imageUrl
            firebaseFirestore.collection("categories").add(category).addOnSuccessListener {
                trySend(ResultState.Success("Thêm danh mục thành công"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
                Log.e("FirestoreError", "Error adding category: ${it.message}", it)
            }
            Log.d("FirestoreDebug", "Successfully added category: ${category.name}")
        } else {
            trySend(ResultState.Error("Upload ảnh thất bại"))
        }
        awaitClose{
            close()
        }
    }

    override fun deleteCategory(categoryName: String): Flow<ResultState<CategoryDataModels>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("categories")
            .whereEqualTo("name", categoryName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val docRef = querySnapshot.documents[0].reference
                    docRef.delete()
                        .addOnSuccessListener {
                            trySend(ResultState.Success(CategoryDataModels()))
                        }
                        .addOnFailureListener {
                            trySend(ResultState.Error(it.message ?: "Unknown error during delete"))
                            Log.e("FirestoreError", "Delete failed: ${it.message}", it)
                        }
                } else {
                    trySend(ResultState.Error("Category not found"))
                    Log.e("FirestoreError", "No document found for category: $categoryName")
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Error fetching category"))
                Log.e("FirestoreError", "Error fetching document: ${it.message}", it)
            }

        awaitClose { close() }
    }

    override fun updateCategory(category: CategoryDataModels, categoryName: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        // thực hiện cập nhật sản phẩm cũng như tải ảnh lên imgbb
        val imageUrl = uploadImageToImgBB(Uri.parse(category.categoryImage))

        if (imageUrl != null) {
            category.categoryImage = imageUrl

            // Tìm document có name = categoryName
            firebaseFirestore.collection("categories")
                .whereEqualTo("name", categoryName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val docRef = querySnapshot.documents[0].reference
                        docRef.set(category)
                            .addOnSuccessListener {
                                trySend(ResultState.Success("Cập nhật danh mục thành công"))
                            }
                            .addOnFailureListener {
                                trySend(ResultState.Error(it.message ?: "Unknown error"))
                                Log.e("FirestoreError", "Error updating category: ${it.message}", it)
                            }
                    } else {
                        trySend(ResultState.Error("Không tìm thấy danh mục để cập nhật"))
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Lỗi khi tìm danh mục"))
                }
        } else {
            trySend(ResultState.Error("Upload ảnh thất bại"))
        }

        awaitClose { close() }
    }

    override fun getAllBanners(): Flow<ResultState<List<BannerDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        // lấy tất cả dữ liệu từ name , image của banners từ firebasestore
        firebaseFirestore.collection("banners").get().addOnSuccessListener{
                querySnapshot->
            val banners = querySnapshot.documents.mapNotNull { document ->
                document.toObject(BannerDataModels::class.java)
            }
            trySend(ResultState.Success(banners))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
        }
        awaitClose{
            close()
        }
    }

    override fun getBannerByName(bannerName: String): Flow<ResultState<BannerDataModels>> = callbackFlow{
        trySend(ResultState.Loading)
        // lấy dữ liệu từ name , image theo name của banners từ firebasestore
        firebaseFirestore.collection("banners")
            .whereEqualTo("name", bannerName)
            .get()
            .addOnSuccessListener{
                querySnapshot->
            val banner = querySnapshot.documents.mapNotNull { document ->
                document.toObject(BannerDataModels::class.java)
            }
            trySend(ResultState.Success(banner[0]))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
        }
        awaitClose{
            close()
        }
    }

    override fun addBanner(banner: BannerDataModels): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // thực hiện thêm name , image của banners vào firebasestore
        val imageUrl = uploadImageToImgBB(Uri.parse(banner.image))
        if (imageUrl != null) {
            banner.image = imageUrl
            firebaseFirestore.collection("banners")
                .add(banner)
                .addOnSuccessListener {
                trySend(ResultState.Success("Thêm Banner thành công"))
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
                Log.e("FirestoreError", "Error adding category: ${it.message}", it)
            }
            Log.d("FirestoreDebug", "Successfully added category: ${banner.name}")
        } else {
            trySend(ResultState.Error("Upload ảnh thất bại"))
        }
        awaitClose{
            close()
        }
    }

    override fun deleteBanner(bannerName: String): Flow<ResultState<BannerDataModels>>  = callbackFlow{
        trySend(ResultState.Loading)
        // xóa banner theo name của banners từ firebasestore
        firebaseFirestore.collection("banners")
            .whereEqualTo("name", bannerName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val docRef = querySnapshot.documents[0].reference
                    docRef.delete()
                        .addOnSuccessListener {
                            trySend(ResultState.Success(BannerDataModels()))
                        }
                        .addOnFailureListener {
                            trySend(ResultState.Error(it.message ?: "Unknown error during delete"))
                            Log.e("FirestoreError", "Delete failed: ${it.message}", it)
                        }
                } else {
                    trySend(ResultState.Error("Banner not found"))
                    Log.e("FirestoreError", "No document found for category: $bannerName")
                }
            }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Error fetching category"))
                Log.e("FirestoreError", "Error fetching document: ${it.message}", it)
            }
        awaitClose { close() }
    }

    override fun updateBanner(banner: BannerDataModels, bannerName: String): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // cập nhật banner theo name của banners từ firebasestore
        val imageUrl = uploadImageToImgBB(Uri.parse(banner.image))

        if (imageUrl != null) {
            banner.image = imageUrl
            firebaseFirestore.collection("banners")
                .whereEqualTo("name", bannerName)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val docRef = querySnapshot.documents[0].reference
                        docRef.set(banner)
                            .addOnSuccessListener {
                                trySend(ResultState.Success("Cập nhật banner thành công"))
                            }
                            .addOnFailureListener {
                                trySend(ResultState.Error(it.message ?: "Unknown error"))
                                Log.e("FirestoreError", "Error updating category: ${it.message}", it)
                            }
                    } else {
                        trySend(ResultState.Error("Không tìm thấy banner để cập nhật"))
                    }
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(it.message ?: "Lỗi khi tìm banner"))
                }
        } else {
            trySend(ResultState.Error("Upload ảnh thất bại"))
        }

        awaitClose { close() }

    }

    override fun getAllUsers(): Flow<ResultState<List<UserDataParent>>> = callbackFlow{
        trySend(ResultState.Loading)
        // Lấy tất cả các dữ liệu từ users từ firebasestore
        firebaseFirestore.collection(USER_COLLECTION).get().addOnSuccessListener{
                querySnapshot->
            val users = querySnapshot.documents.mapNotNull { document ->
                UserDataParent(
                    nodeId = document.id,
                    userData = document.toObject(UserData::class.java) ?: UserData()
                )
            }
            trySend(ResultState.Success(users))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
        }
        awaitClose{
            close()
        }
    }

    override fun getUserByEmail(email: String): Flow<ResultState<UserDataParent>> = callbackFlow{
        trySend(ResultState.Loading)
        // lấy dữ liệu người dùng theo email từ firebasestore
        firebaseFirestore.collection(USER_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val user = UserDataParent(
                        nodeId = document.id,
                        userData = document.toObject(UserData::class.java) ?: UserData()
                        )
                    trySend(ResultState.Success(user))
                } else {
                    trySend(ResultState.Error("User not found"))
                }
                Log.d("FirestoreDebug", "Successfully fetched user: $email")
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
                Log.e("FirestoreError", "Error fetching user: ${it.message}", it)
            }
        awaitClose {
            close()
        }
    }

    override fun exportUserPdfByEmail(email: String): Flow<ResultState<UserDataParent>> = callbackFlow{
        trySend(ResultState.Loading)
        // lấy dữ liệu người dùng theo email từ firebasestore để in ra file pdf
        firebaseFirestore.collection(USER_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val user = UserDataParent(
                        nodeId = document.id,
                        userData = document.toObject(UserData::class.java) ?: UserData()
                        )
                    trySend(ResultState.Success(user))
                } else {
                    trySend(ResultState.Error("User not found"))
                }
                Log.d("FirestoreDebug", "Successfully fetched user: $email")
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Unknown error"))
                Log.e("FirestoreError", "Error fetching user: ${it.message}", it)
            }
        awaitClose {
            close()
        }
    }

    override fun deleteUser(email: String): Flow<ResultState<UserDataParent>> = callbackFlow {
        trySend(ResultState.Loading)
        // xóa người dùng theo email từ firebasestore
        firebaseFirestore.collection(USER_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val docRef = querySnapshot.documents[0].reference
                    docRef.delete()
                        .addOnSuccessListener {
                            trySend(ResultState.Success(UserDataParent()))
                            Log.d("FirestoreDebug", "Successfully deleted user: $email")
                        }
                        .addOnFailureListener {
                            trySend(ResultState.Error(it.message ?: "Unknown error during delete"))
                            Log.e("FirestoreError", "Delete failed: ${it.message}", it)
                        }
                } else {
                    trySend(ResultState.Error("User not found"))
                    Log.e("FirestoreError", "No document found for user: $email")
                    }
                }
            .addOnFailureListener {
                trySend(ResultState.Error(it.message ?: "Error fetching user"))
                Log.e("FirestoreError", "Error fetching document: ${it.message}", it)
            }
        awaitClose {
            close()
        }
    }

    override fun getAllOrders(): Flow<ResultState<List<OrderDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        Log.d("FirestoreDebug", "Bắt đầu lấy tất cả đơn hàng từ danh sách người dùng...")

        // Bước 1: Lấy danh sách người dùng
        firebaseFirestore.collection(USER_COLLECTION).get()
            .addOnSuccessListener { querySnapshot ->
                val userIds = querySnapshot.documents.map { it.id }

                if (userIds.isEmpty()) {
                    Log.d("FirestoreDebug", "Không có người dùng nào.")
                    trySend(ResultState.Success(emptyList()))
                    close()
                    return@addOnSuccessListener
                }

                val combinedOrders = mutableListOf<OrderDataModels>()
                var processedUsers = 0
                val totalUsers = userIds.size

                for (userId in userIds) {
                    Log.d("FirestoreDebug", "Đang lấy đơn hàng cho userId: $userId")

                    // Bước 2: Truy vấn đơn hàng của từng user từ orders/userId/User_Order
                    firebaseFirestore.collection("orders")
                        .document(userId)
                        .collection("User_Order")
                        .get()
                        .addOnSuccessListener { orderDocs ->
                            Log.d("FirestoreDebug", "userId=$userId có ${orderDocs.size()} đơn hàng.")

                            for (orderDoc in orderDocs.documents) {
                                val order = orderDoc.toObject(OrderDataModels::class.java)
                                if (order != null) {
                                    combinedOrders.add(order)
                                    Log.d("FirestoreDebug", "Lấy thành công đơn hàng ${orderDoc.id} của userId=$userId")
                                } else {
                                    Log.e("FirestoreDebug", "Không thể parse đơn hàng ${orderDoc.id} của userId=$userId")
                                }
                            }

                            processedUsers++
                            if (processedUsers == totalUsers) {
                                Log.d("FirestoreDebug", "Đã xử lý xong tất cả người dùng.")
                                trySend(ResultState.Success(combinedOrders))
                                close()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Lỗi khi lấy đơn hàng của userId=$userId: ${e.message}")
                            processedUsers++
                            if (processedUsers == totalUsers) {
                                trySend(ResultState.Success(combinedOrders))
                                close()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy danh sách người dùng: ${e.message}")
                trySend(ResultState.Error(e.message ?: "Lỗi không xác định khi lấy danh sách người dùng."))
                close()
            }

        awaitClose { close() }
    }

    override fun getOrderByCode(postalCode: String): Flow<ResultState<OrderDataModels>> = callbackFlow {
        trySend(ResultState.Loading)
        Log.d("FirestoreDebug", "Bắt đầu tìm đơn hàng với postalCode=$postalCode từ danh sách người dùng...")

        firebaseFirestore.collection(USER_COLLECTION).get()
            .addOnSuccessListener { querySnapshot ->
                val userIds = querySnapshot.documents.map { it.id }

                if (userIds.isEmpty()) {
                    Log.d("FirestoreDebug", "Không có người dùng nào.")
                    trySend(ResultState.Error("Không có người dùng nào."))
                    close()
                    return@addOnSuccessListener
                }

                var found = false
                var processedUsers = 0
                val totalUsers = userIds.size

                for (userId in userIds) {
                    if (found) break // Dừng vòng lặp nếu đã tìm thấy

                    firebaseFirestore.collection("orders")
                        .document(userId)
                        .collection("User_Order")
                        .whereEqualTo("postalCode", postalCode)
                        .get()
                        .addOnSuccessListener { orderDocs ->
                            if (!found && !orderDocs.isEmpty) {
                                val order = orderDocs.documents.firstOrNull()?.toObject(OrderDataModels::class.java)
                                if (order != null) {
                                    found = true
                                    Log.d("FirestoreDebug", "✅ Tìm thấy đơn hàng có postalCode=$postalCode cho userId=$userId")
                                    trySend(ResultState.Success(order))
                                    close()
                                    return@addOnSuccessListener
                                }
                            }

                            processedUsers++
                            if (processedUsers == totalUsers && !found) {
                                trySend(ResultState.Error("Không tìm thấy đơn hàng với postalCode=$postalCode."))
                                close()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Lỗi khi truy vấn đơn hàng userId=$userId: ${e.message}")
                            processedUsers++
                            if (processedUsers == totalUsers && !found) {
                                trySend(ResultState.Error("Lỗi khi truy vấn đơn hàng: ${e.message}"))
                                close()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy danh sách người dùng: ${e.message}")
                trySend(ResultState.Error("Lỗi khi lấy danh sách người dùng: ${e.message}"))
                close()
            }

        awaitClose { close() }
    }

    override fun updateOrder(order: OrderDataModels, postalCode: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val userCollection = firebaseFirestore.collection(USER_COLLECTION)

        try {
            val userSnapshot = userCollection.get().await()
            val userIds = userSnapshot.documents.map { it.id }

            if (userIds.isEmpty()) {
                trySend(ResultState.Error("Không có người dùng nào."))
                close()
                return@callbackFlow
            }

            for (userId in userIds) {
                val orderSnapshot = firebaseFirestore.collection("orders")
                    .document(userId)
                    .collection("User_Order")
                    .whereEqualTo("postalCode", postalCode)
                    .get()
                    .await()

                if (!orderSnapshot.isEmpty) {
                    val docRef = orderSnapshot.documents.first().reference

                    try {
                        docRef.set(order).await()
                        Log.d("FirestoreDebug", "✅ Cập nhật thành công đơn hàng với postalCode=$postalCode cho userId=$userId")
                        trySend(ResultState.Success("Cập nhật đơn hàng thành công"))
                    } catch (e: Exception) {
                        Log.e("FirestoreError", "❌ Lỗi khi cập nhật đơn hàng: ${e.message}")
                        trySend(ResultState.Error("Lỗi khi cập nhật đơn hàng: ${e.message}"))
                    }
                    close()
                    return@callbackFlow
                }
            }

            trySend(ResultState.Error("Không tìm thấy đơn hàng với postalCode=$postalCode."))
            close()
        } catch (e: Exception) {
            Log.e("FirestoreError", "❌ Lỗi Firestore: ${e.message}")
            trySend(ResultState.Error("Lỗi Firestore: ${e.message}"))
            close()
        }

        awaitClose { close() }
    }

    override fun deleteOrder(postalCode: String): Flow<ResultState<OrderDataModels>> = callbackFlow{
        trySend(ResultState.Loading)
        Log.d("FirestoreDebug", "Bắt đầu tìm đơn hàng với postalCode=$postalCode từ danh sách người dùng để xóa...")

        firebaseFirestore.collection(USER_COLLECTION).get()
            .addOnSuccessListener { querySnapshot ->
                val userIds = querySnapshot.documents.map { it.id }

                if (userIds.isEmpty()) {
                    Log.d("FirestoreDebug", "Không có người dùng nào.")
                    trySend(ResultState.Error("Không có người dùng nào."))
                    close()
                    return@addOnSuccessListener
                }
                var found = false
                var processedUsers = 0
                val totalUsers = userIds.size
                for (userId in userIds) {
                    if (found) break // Dừng vòng lặp nếu đã tìm thấy
                    firebaseFirestore.collection("orders")
                        .document(userId)
                        .collection("User_Order")
                        .whereEqualTo("postalCode", postalCode)
                        .get()
                        .addOnSuccessListener { orderDocs ->
                            if (!found && !orderDocs.isEmpty) {
                                val docRef = orderDocs.documents.first().reference
                                docRef.delete()
                                    .addOnSuccessListener {
                                        found = true
                                        Log.d("FirestoreDebug", "✅ Xóa thành công đơn hàng với postalCode=$postalCode cho userId=$userId")
                                        trySend(ResultState.Success(OrderDataModels()))
                                        close()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("FirestoreError", "❌ Lỗi khi xóa đơn hàng: ${e.message}")
                                        trySend(ResultState.Error("Lỗi khi xóa đơn hàng: ${e.message}"))
                                        close()
                                    }
                            }
                            processedUsers++
                            if (processedUsers == totalUsers && !found) {
                                trySend(ResultState.Error("Không tìm thấy đơn hàng với postalCode=$postalCode."))
                                close()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Lỗi khi truy vấn đơn hàng userId=$userId: ${e.message}")
                            processedUsers++
                            if (processedUsers == totalUsers && !found) {
                                trySend(ResultState.Error("Lỗi khi truy vấn đơn hàng: ${e.message}"))
                                close()
                            }
                            close()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Lỗi khi lấy danh sách người dùng: ${e.message}")
                trySend(ResultState.Error("Lỗi khi lấy danh sách người dùng: ${e.message}"))
                close()
            }
        awaitClose{
            close()
        }
    }

    override fun countAllProducts(): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // đếm tất cả sản phẩm từ firebasestore
        firebaseFirestore.collection(PRODUCTS_COLLECTION).get().addOnSuccessListener{
                querySnapshot->
            val count = querySnapshot.documents.size
            trySend(ResultState.Success(count.toString()))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
            Log.e("FirestoreError", "Error counting products: ${exception.message}", exception)
            close()
        }
        awaitClose {
            close()
            Log.d("FirestoreDebug", "CallbackFlow closed")
        }
    }

    override fun countAllCategories(): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // đếm tất cả danh mục từ firebasestore
        firebaseFirestore.collection("categories").get().addOnSuccessListener{
                querySnapshot->
            val count = querySnapshot.documents.size
            trySend(ResultState.Success(count.toString()))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
            Log.e("FirestoreError", "Error counting categories: ${exception.message}", exception)
            close()
        }
        awaitClose {
            close()
            Log.d("FirestoreDebug", "CallbackFlow closed")
        }
    }

    override fun countAllBanners(): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // đếm tất cả banner từ firebasestore
        firebaseFirestore.collection("banners").get().addOnSuccessListener{
                querySnapshot->
            val count = querySnapshot.documents.size
            trySend(ResultState.Success(count.toString()))
        }.addOnFailureListener{
                exception ->
            trySend(ResultState.Error(exception.message ?: "Unknown error"))
            Log.e("FirestoreError", "Error counting banners: ${exception.message}", exception)
            close()
        }
        awaitClose {
            close()
            Log.d("FirestoreDebug", "CallbackFlow closed")
        }
    }

    override fun countAllOrders(): Flow<ResultState<String>> = callbackFlow{
        trySend(ResultState.Loading)
        // đếm tất cả đơn hàng từ tất cả người dùng có các đơn hàng "Đang kiểm duyệt" firebasestore
        firebaseFirestore.collection(USER_COLLECTION).get()
            .addOnSuccessListener { querySnapshot ->
                val userIds = querySnapshot.documents.map { it.id }

                if (userIds.isEmpty()) {
                    Log.d("FirestoreDebug", "Không có người dùng nào.")
                    trySend(ResultState.Error("Không có người dùng nào."))
                    close()
                    return@addOnSuccessListener
                }
                var count = 0
                var processedUsers = 0
                val totalUsers = userIds.size
                for (userId in userIds) {
                    firebaseFirestore.collection("orders")
                        .document(userId)
                        .collection("User_Order")
                        .whereEqualTo("statusBill", "Đang kiểm duyệt")
                        .get()
                        .addOnSuccessListener { orderDocs ->
                            count += orderDocs.size()
                            processedUsers++
                            if (processedUsers == totalUsers) {
                                trySend(ResultState.Success(count.toString()))
                                close()
                                Log.d("FirestoreDebug", "Đã đếm xong tất cả đơn hàng.")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Lỗi khi đếm đơn hàng userId=$userId: ${e.message}")
                            processedUsers++
                            if (processedUsers == totalUsers) {
                                trySend(ResultState.Error("Lỗi khi đếm đơn hàng: ${e.message}"))
                                close()
                            }
                            close()
                        }
                }
            }
        awaitClose {
            close()
            Log.d("FirestoreDebug", "CallbackFlow closed")
        }

    }

    override fun getAllOrderByLineChar(): Flow<ResultState<List<OrderDataModels>>> = callbackFlow {
        trySend(ResultState.Loading)
        Log.d("LineChartDebug", "Bắt đầu lấy dữ liệu đơn hàng cho LineChart...")

        firebaseFirestore.collection(USER_COLLECTION).get()
            .addOnSuccessListener { querySnapshot ->
                val userIds = querySnapshot.documents.map { it.id }

                if (userIds.isEmpty()) {
                    trySend(ResultState.Success(emptyList()))
                    close()
                    return@addOnSuccessListener
                }

                val filteredOrders = mutableListOf<OrderDataModels>()
                var processedUsers = 0
                val totalUsers = userIds.size

                for (userId in userIds) {
                    firebaseFirestore.collection("orders")
                        .document(userId)
                        .collection("User_Order")
                        .get()
                        .addOnSuccessListener { orderDocs ->
                            for (orderDoc in orderDocs.documents) {
                                val order = orderDoc.toObject(OrderDataModels::class.java)
                                if (order != null) {
                                    // Chỉ lấy các đơn hàng có date và totalPrice hợp lệ
                                    if (order.date.isNotBlank() && order.totalPrice > 0.0) {
                                        filteredOrders.add(
                                            OrderDataModels(
                                                date = order.date,
                                                totalPrice = order.totalPrice
                                            )
                                        )
                                    }
                                }
                            }

                            processedUsers++
                            if (processedUsers == totalUsers) {
                                Log.d("LineChartDebug", "Đã lấy xong dữ liệu cho $totalUsers người dùng.")
                                trySend(ResultState.Success(filteredOrders))
                                close()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LineChartError", "Lỗi lấy đơn hàng cho userId=$userId: ${e.message}")
                            processedUsers++
                            if (processedUsers == totalUsers) {
                                trySend(ResultState.Success(filteredOrders))
                                close()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("LineChartError", "Lỗi lấy danh sách người dùng: ${e.message}")
                trySend(ResultState.Error(e.message ?: "Không thể lấy dữ liệu"))
                close()
            }

        awaitClose { close() }
    }



}