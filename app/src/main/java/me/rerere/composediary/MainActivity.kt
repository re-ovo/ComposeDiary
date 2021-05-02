package me.rerere.composediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.rerere.composediary.ui.page.EditPage
import me.rerere.composediary.ui.page.Index
import me.rerere.composediary.ui.theme.ComposeDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProvideWindowInsets {
                ComposeDiaryTheme {
                    val navController = rememberNavController()
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = MaterialTheme.colors.isLight
                    val systemColor = MaterialTheme.colors.primaryVariant
                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = systemColor,
                            darkIcons = useDarkIcons
                        )
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "index"
                    ) {
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
}