package com.example.adminlaptopapp.presentation.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class ImageUploadHelper {

    companion object {
        private const val IMGBB_API_KEY = "cfb88fd6087fa222b489b186dff8c38d" // Thay thế bằng API key của bạn
        private const val IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload"
        private const val TAG = "ImageUploadHelper"
    }

    suspend fun uploadImageToImgBB(context: Context, imageUri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Đọc hình ảnh từ URI
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                if (inputStream == null) {
                    return@withContext Result.failure(Exception("Không thể đọc hình ảnh"))
                }

                // Chuyển đổi thành byte array
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                val imageBytes = byteArrayOutputStream.toByteArray()
                inputStream.close()

                // Encode thành Base64
                val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                // Tạo request body
                val requestBody = FormBody.Builder()
                    .add("key", IMGBB_API_KEY)
                    .add("image", base64Image)
                    .build()

                // Tạo request
                val request = Request.Builder()
                    .url(IMGBB_UPLOAD_URL)
                    .post(requestBody)
                    .build()

                // Thực hiện request
                val client = OkHttpClient()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        if (jsonObject.getBoolean("success")) {
                            val imageUrl = jsonObject.getJSONObject("data").getString("url")
                            Log.d(TAG, "Upload thành công: $imageUrl")
                            return@withContext Result.success(imageUrl)
                        } else {
                            val error = jsonObject.optString("error", "Upload failed")
                            Log.e(TAG, "Upload thất bại: $error")
                            return@withContext Result.failure(Exception("Upload thất bại: $error"))
                        }
                    }
                }

                Log.e(TAG, "Response không thành công: ${response.code}")
                return@withContext Result.failure(Exception("Upload thất bại với mã lỗi: ${response.code}"))

            } catch (e: IOException) {
                Log.e(TAG, "Lỗi IO khi upload", e)
                return@withContext Result.failure(e)
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi không xác định khi upload", e)
                return@withContext Result.failure(e)
            }
        }
    }
}