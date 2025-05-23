package com.example.adminlaptopapp.presentation.screens.banner

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.adminlaptopapp.R
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.presentation.navigations.Routes
import com.example.adminlaptopapp.presentation.screens.category.CategoryCard
import com.example.adminlaptopapp.presentation.screens.product.SmallIconButton
import com.example.adminlaptopapp.presentation.viewModels.AdminLaptopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllBannerScreenUi(
    navController: NavController,
    viewModel: AdminLaptopViewModel = hiltViewModel(),
) {

    val getAllBannerState = viewModel.getAllBannerState.collectAsStateWithLifecycle()
    val bannerData = getAllBannerState.value.bannersData ?: emptyList()

    LaunchedEffect(key1 = Unit) {
        viewModel.getAllBanner()
    }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filteredBanner by remember { mutableStateOf(bannerData) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            // Tìm kiếm các sản phẩm theo từ khóa
            filteredBanner = bannerData.filter { product ->
                // Kiểm tra nếu tên chứa từ khóa
                product?.name?.contains(searchQuery, ignoreCase = true) == true
            }
        } else {
            // Nếu không có từ khóa tìm kiếm, hiển thị tất cả sản phẩm
            filteredBanner = bannerData
        }
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Tất cả Banner", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                    },
                    modifier = Modifier
                        .padding(8.dp),
                    placeholder = { Text("Tìm kiếm") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        navController.navigate(Routes.AddBannerScreen)
                    },
                    modifier = Modifier
                        .background(color = colorResource(id = R.color.orange), shape = CircleShape)
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp)

                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Product",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            when{
                getAllBannerState.value.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                getAllBannerState.value.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Xin lỗi, không thể lấy được thông tin banner.")
                    }
                }

                bannerData.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Không có banner nào cả")
                    }
                }

                else ->{
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = "Tên Banner",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(0.45f))
                        Text(
                            text = "Hình ảnh",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(0.1f))
                        Text(
                            text = "Chức năng",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                        Spacer(modifier = Modifier.weight(.15f))
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ){
                        if (filteredBanner.isEmpty()){
                            items(bannerData) { item ->
                                BannerCard(
                                    banner = item!!,
                                    onShowDetailsClick = {
                                        navController.navigate(Routes.GetBannerByNameScreen(item.name))
                                    },
                                    onEditClick = {
                                        navController.navigate(Routes.UpdateBannerScreen(item.name))
                                    },
                                    onDeleteClick = {
                                        viewModel.deleteBanner(item.name)
                                    }
                                )
                            }
                        }else{
                            items(filteredBanner) { item ->
                                if (item != null) {
                                    BannerCard(
                                        banner = item,
                                        onShowDetailsClick = {
                                            navController.navigate(Routes.GetBannerByNameScreen(item.name))
                                        },
                                        onEditClick = {
                                            navController.navigate(Routes.UpdateBannerScreen(item.name))
                                        },
                                        onDeleteClick = {
                                            viewModel.deleteBanner(item.name)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }


            }
        }

    }


}

@Composable
fun BannerCard(
    banner: BannerDataModels,
    onShowDetailsClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = banner.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.2f)
            )

            Spacer(modifier = Modifier.width(13.dp))

            AsyncImage(
                model = banner.image,
                contentDescription = banner.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(130.dp)
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.height(120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly // Các nút cách đều nhau
            ) {
                SmallIconButton(
                    onClick = onShowDetailsClick,
                    backgroundColor = Color(0xFF4CAF50),
                    icon = Icons.Default.RemoveRedEye,
                    contentDescription = "Xem"
                )
                SmallIconButton(
                    onClick = onEditClick,
                    backgroundColor = Color(0xFFFFC107),
                    icon = Icons.Default.Edit,
                    contentDescription = "Sửa"
                )
                SmallIconButton(
                    onClick = onDeleteClick,
                    backgroundColor = Color(0xFFF44336),
                    icon = Icons.Default.Delete,
                    contentDescription = "Xóa"
                )
            }

        }
    }
}