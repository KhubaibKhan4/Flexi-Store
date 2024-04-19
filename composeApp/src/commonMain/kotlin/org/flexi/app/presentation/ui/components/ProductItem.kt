package org.flexi.app.presentation.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.flexi.app.domain.model.products.Products
import org.flexi.app.presentation.ui.screens.detail.common.DetailScreen
import org.flexi.app.utils.Constant.BASE_URL

@Composable
fun ProductList(
    products: List<Products>,
    state: LazyGridState = rememberLazyGridState(),
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (products.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
                    .heightIn(550.dp)
                    .padding(bottom = 34.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No items found",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "No items found",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                state = state,
                modifier = modifier.fillMaxWidth()
                    .height(1200.dp).padding(bottom = 34.dp, top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { pro ->
                    ProductItem(pro)
                }
            }
        }
    }

}

@Composable
fun ProductItem(
    products: Products,
) {
    val navigator = LocalNavigator.current
    val image: Resource<Painter> =
        asyncPainterResource(data = BASE_URL + products.imageUrl)
    Column(
        modifier = Modifier.fillMaxWidth()
            .border(
                border = BorderStroke(width = 1.dp, color = Color.LightGray),
                shape = RoundedCornerShape(14.dp),

                )
            .clickable {
                navigator?.push(DetailScreen(products))
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            KamelImage(
                resource = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop,
                animationSpec = tween(),
                onLoading = {
                    CircularProgressIndicator()
                },
                onFailure = {
                    Text("Failed to Load")
                }
            )
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(top = 6.dp, end = 8.dp)
                    .size(25.dp)
                    .clip(CircleShape)
                    .background(
                        Color.DarkGray.copy(alpha = 0.65f)
                    )
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = products.name,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 3.dp, end = 3.dp)
            )
            Text(
                text = products.categoryTitle,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "$" + products.price,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}