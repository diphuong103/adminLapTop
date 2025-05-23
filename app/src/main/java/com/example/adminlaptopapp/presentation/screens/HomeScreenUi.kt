package com.example.adminlaptopapp.presentation.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes

import android.view.ViewGroup
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adminlaptopapp.presentation.screens.product.formatCurrencyVND
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun HomeScreenUi(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel(),
) {
    val homeState by viewModel.homeScreenState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val totalRevenue = homeState.totalRevenue ?: "0"
    val hasNewMessages by viewModel.hasNewMessages.collectAsStateWithLifecycle()

    if (homeState.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (homeState.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = homeState.errorMessage!!, modifier = Modifier.align(Alignment.Center))
        }
    } else{
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("TRANG CHỦ") },
                    actions = {
                        ChatIconWithBadge(
                            hasNewMessages = hasNewMessages,
                            onClick = { navController.navigate(Routes.ChatListScreen) }
                        )
                    }
                )

            }
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = R.drawable._d_computer),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp),
                            contentScale = ContentScale.Crop
                        )


                        Text(
                            text = "Chào mừng Admin",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "đã đến quản lý\nLaptop Store",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Vouchers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(Color(0xFFC8E6C9), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate(Routes.VoucherScreen)

                                }
                        )
                    }
                }

                item {
                    CardItem(
                        count = "S.Phẩm: ${homeState.products.toString()}",
                        description = "Tổng số sản phẩm",
                        backgroundColor = Color(0xFFE3F2FD),
                        onClickCard = {
                            navController.navigate(Routes.GetAllProductScreen)
                        }
                    )
                }
                item {
                    CardItem(
                        count = "C.Mục: ${homeState.categories.toString()}",
                        description = "Tổng số danh mục",
                        backgroundColor = Color(0xFFFFF3E0),
                        onClickCard = {
                            navController.navigate(Routes.GetAllCategoryScreen)
                        }
                    )
                }
                item {
                    CardItem(
                        count = "Banner: ${homeState.banners.toString()}",
                        description = "Tổng số Banner\nảnh quảng cáo\nảnh giới thiệu",
                        backgroundColor = Color(0xFFE8F5E9),
                        onClickCard = {
                            navController.navigate(Routes.GetAllBannerScreen)
                        }
                    )
                }
                item {
                    CardItem(
                        count = "Đ.Hàng: ${homeState.orders.toString()}",
                        description = "Số đơn hàng\nđang kiểm\nduyệt",
                        backgroundColor = Color(0xFFFFEBEE),
                        onClickCard = {
                            navController.navigate(Routes.GetAllOrderScreen)
                        }
                    )
                }

                item(span = { GridItemSpan(2) }){
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = { navController.navigate(Routes.GetAllUserScreen) }),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            // màu light orange
                            containerColor = Color(0xFFFFE0B2)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Doanh thu: ${formatCurrencyVND(totalRevenue.toDouble())} vnd",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Divider(thickness = 1.dp)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Xem tất cả người dùng App Laptop Store",
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Biểu đồ thống kê",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (!homeState.lineChartOrders.isNullOrEmpty()) {
                            OrderLineChart(homeState.lineChartOrders!!, isLandscape)
                        } else {
                            Text(
                                text = "Không có dữ liệu để hiển thị.",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderLineChart(orderList: List<OrderDataModels>, isLandscape: Boolean) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    600
                )
                setBackgroundColor(Color.White.toArgb())
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                axisRight.isEnabled = false
                legend.isEnabled = true
            }
        },
        update = { chart ->
            // Gộp các đơn theo ngày và cộng dồn totalPrice (giữ nguyên kiểu Double)
            val groupedData = orderList
                .groupBy { it.date }
                .mapValues { entry -> entry.value.sumOf { it.totalPrice } }
                .toList()
                .sortedBy { entry ->
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(entry.first)
                }

            // Tùy chỉnh số lượng dữ liệu hiển thị tùy vào hướng màn hình
            val displayData = if (isLandscape) {
                groupedData  // Hiển thị tất cả dữ liệu nếu là landscape
            } else {
                groupedData.take(7)  // Giới hạn hiển thị chỉ 7 ngày nếu là portrait
            }

            // Tạo entries cho biểu đồ
            val entries = displayData.mapIndexed { index, (date, total) ->
                Entry(index.toFloat(), total.toFloat())
            }

            val dataSet = LineDataSet(entries, "Tổng tiền theo ngày").apply {
                color = Color.Blue.toArgb()
                valueTextColor = Color.Black.toArgb()
                lineWidth = 2f
                setDrawCircles(true)
                setCircleColor(Color.Red.toArgb())
                circleRadius = 4f
                mode = LineDataSet.Mode.LINEAR
                valueTextSize = 10f
            }

            // Thiết lập cho trục X (ngày)
            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < displayData.size) {
                            displayData[index].first  // Trả về ngày
                        } else ""
                    }
                }
            }

            // Thiết lập cho trục Y
            chart.axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f  // Đảm bảo giá trị tối thiểu của trục Y là 0
            }

            // Cập nhật dữ liệu cho biểu đồ
            chart.data = LineData(dataSet)
            chart.invalidate()  // Vẽ lại biểu đồ
        }
    )
}

@Composable
fun CardItem(
    count: String,
    description: String,
    backgroundColor: Color,
    onClickCard: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClickCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Divider(thickness = 1.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ChatIconWithBadge(
    hasNewMessages: Boolean,
    onClick: () -> Unit,
    badgeSize: Dp = 10.dp,
) {
    Box(
        modifier = Modifier
            .size(48.dp) // kích thước vùng nhấn
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "Chat",
            modifier = Modifier.size(32.dp)
        )
        if (hasNewMessages) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }
}