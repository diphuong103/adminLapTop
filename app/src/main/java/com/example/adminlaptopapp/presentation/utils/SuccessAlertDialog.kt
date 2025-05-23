package com.example.adminlaptopapp.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adminlaptopapp.ui.theme.AdminLaptopAppTheme
import com.example.adminlaptopapp.R
//import com.example.shoppinglaptopapp.ui.theme.ShoppingLaptopAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessAlertDialog(
    onClick : () -> Unit,
    textTitle : String,
    textButton : String
) {
    BasicAlertDialog(
        onDismissRequest = {},
        modifier = Modifier.background(shape = RoundedCornerShape(10.dp) , color = Color.White),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(color = colorResource(id = R.color.orange), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Thành công",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "THÀNH CÔNG",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.orange)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Xin chúc mừng \n bạn đã $textTitle",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = textButton,
                        color = Color.Gray
                    )
                }

            }
        }
    )
}

@Preview
@Composable
fun SuccessAlertDialogShow() {
    AdminLaptopAppTheme {
        SuccessAlertDialog(
            textTitle = "Theem",
            textButton = "OK" ,
            onClick = {}
        )
    }
}