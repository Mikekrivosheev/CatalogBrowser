package com.mikhailkrivosheev.catalogbrowser.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mikhailkrivosheev.catalogbrowser.ui.catalog.ListRoute
import com.mikhailkrivosheev.catalogbrowser.ui.details.DetailsRoute
import com.mikhailkrivosheev.catalogbrowser.ui.theme.CatalogBrowserTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            CatalogBrowserTheme {
                CatalogBrowserNavHost(navController)
            }
        }
    }
}


@Composable
private fun CatalogBrowserNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = listScreenRoute) {
        composable(listScreenRoute) {
            ListRoute {
                navController.navigate(detailsScreenRoute(it.id))
            }
        }
        composable(
            route = detailsScreenRoute,
            arguments = listOf(navArgument(itemIdArg) { type = NavType.StringType })
        ) {
            DetailsRoute()
        }
    }
}

private const val detailsScreenName = "detailsScreenName"

const val itemIdArg = "itemIdArg"

private const val listScreenRoute = "listScreenRoute"
private const val detailsScreenRoute = "$detailsScreenName/{$itemIdArg}"

private fun detailsScreenRoute(itemId: String): String {
    return "$detailsScreenName/$itemId"
}


