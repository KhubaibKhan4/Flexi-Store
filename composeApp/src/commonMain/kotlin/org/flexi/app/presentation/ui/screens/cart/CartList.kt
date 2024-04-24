package org.flexi.app.presentation.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.flexi.app.domain.model.cart.CartItem
import org.flexi.app.domain.model.products.Products
import org.flexi.app.domain.usecase.ResultState
import org.flexi.app.presentation.ui.components.ErrorBox
import org.flexi.app.presentation.ui.components.LoadingBox
import org.flexi.app.presentation.viewmodels.MainViewModel
import org.flexi.app.utils.Constant.BASE_URL
import org.koin.compose.koinInject

class CartList(
    private val cartItem: List<CartItem>,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val viewModel: MainViewModel = koinInject()
        var product by remember { mutableStateOf<List<Products>?>(null) }

        val ids = cartItem.map { it.productId.toLong() }
        val quantityMap = cartItem.associate { it.productId to it.quantity }

        LaunchedEffect(Unit) {
            viewModel.getProductById(ids)
        }
        val productState by viewModel.productItem.collectAsState()
        when (productState) {
            is ResultState.Error -> {
                val error = (productState as ResultState.Error).error
                ErrorBox(error)
            }

            is ResultState.Loading -> {
                LoadingBox()
            }

            is ResultState.Success -> {
                val response = (productState as ResultState.Success).response
                product = response
            }
        }
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                           navigator?.pop()
                        }
                    )
                    Text(
                        text = "My Cart",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null
                    )
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = it.calculateTopPadding())
            ) {
                product?.let { list ->
                    items(list) { pro ->
                        val quantity = quantityMap[pro.id] ?: 0
                        CartItem(
                            pro,
                            quantity
                        )
                    }
                }

            }
        }

    }

}

@Composable
fun CartItem(
    product: Products,
    quantity: Int
) {
    var isCheck by remember { mutableStateOf(false) }
    var producstItems by remember { mutableStateOf(quantity) }
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(all = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Checkbox(
                checked = isCheck,
                onCheckedChange = {
                     isCheck = it
                },
                modifier = Modifier.size(30.dp),
                enabled = true,
            )
            Spacer(modifier = Modifier.width(8.dp))
            val image: Resource<Painter> =
                asyncPainterResource(BASE_URL + product?.imageUrl.toString())
            KamelImage(
                resource = image,
                contentDescription = null,
                modifier = Modifier.width(125.dp)
                    .height(75.dp)
                    .clip(RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = product?.name.toString(),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val color = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = Color.LightGray,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    ) {
                        append("Color:")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        )
                    ) {
                        append(product?.colors)
                    }
                }
                Text(color)
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .padding(2.dp)
                            .width(84.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = Color.LightGray.copy(alpha = 0.45f)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(25.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    if (producstItems >= 1) {
                                        producstItems--
                                    }
                                }
                        )
                        Text(
                            text = producstItems.toString(),
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    producstItems++
                                }
                        )
                    }
                    val price = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF5821c4),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                baselineShift = BaselineShift.Superscript
                            )
                        ) {
                            append("\$")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("${product?.price}.00")
                        }
                    }
                    Text(
                        text = price,

                        )
                }


            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth(.85f),
            color = Color.LightGray
        )
    }
}