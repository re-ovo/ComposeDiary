package me.rerere.composediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import me.rerere.composediary.ui.Index
import me.rerere.composediary.ui.page.EditPage
import me.rerere.composediary.ui.theme.ComposeDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDiaryTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "index") {
                    // 主页
                    composable("index") {
                        Index(navController)
                    }

                    // 日记编辑页面
                    composable("edit/{id}",
                        arguments = listOf(
                            // 日记ID参数，选择要编辑的日记
                            navArgument("id") {
                                type = NavType.IntType
                            }
                        )) {
                        EditPage(navController, it.arguments?.getInt("id"))
                    }
                }
            }
        }
    }
}