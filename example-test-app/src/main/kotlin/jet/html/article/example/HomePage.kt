package jet.html.article.example

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun HomePage(navHostController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                navHostController.navigate(route = "test")
            }, content = {
                Text(text = "test")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "default")
            }, content = {
                Text(text = "Default")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "simple")
            }, content = {
                Text(text = "Simple")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "mapbox")
            }, content = {
                Text(text = "Mapbox docs")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "android")
            }, content = {
                Text(text = "Android docs")
            }
        )
        Button(
            onClick = {
                navHostController.navigate(route = "wikipedia")
            }, content = {
                Text(text = "Wikipedia")
            }
        )

        Button(
            onClick = {
                navHostController.navigate(route = "medium")
            }, content = {
                Text(text = "Medium")
            }
        )
    }
}
