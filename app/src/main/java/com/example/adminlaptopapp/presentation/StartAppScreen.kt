package com.example.adminlaptopapp.presentation

import android.graphics.LinearGradient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel

@Composable
fun StartAppScreen(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel(),
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 1. Hình nền phía sau
        Image(
            painter = painterResource(id = R.drawable.top_view_pink_keyboard_with_copyspace),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lớp gradient mờ phủ lên
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.grey).copy(alpha = 0.6f),
                            colorResource(id = R.color.orange).copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // 3. Nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Ảnh nhỏ phía trên
            Image(
                painter = painterResource(id = R.drawable.ecommerce_checkout_laptop_rafiki),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tiêu đề
            Text(
                "🎉 Chào mừng Admin! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nội dung phụ
            Text(
                "Đã đến để quản lý cửa hàng ở Laptop Store",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(75.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.HomeScreen)
                },
                modifier = Modifier.size(300.dp , 50.dp).align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Vào ngay",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
