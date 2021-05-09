package me.rerere.composediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.rerere.composediary.ui.page.AboutPage
import me.rerere.composediary.ui.page.EditPage
import me.rerere.composediary.ui.page.Index
import me.rerere.composediary.ui.theme.ComposeDiaryTheme

class MainActivity : ComponentActivity() {
    private val diaryViewModel by viewModels<DiaryViewModel> {
        DiaryViewModelFactory
    }

    @ExperimentalMaterialApi
    @ExperimentalAnimationApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideWindowInsets {
                ComposeDiaryTheme(darkTheme = isSystemInDarkTheme() && diaryViewModel.followSystemDarkMode) {
                    val systemUiController = rememberSystemUiController()
                    val systemColor = MaterialTheme.colors.primary
                    val isLight = MaterialTheme.colors.isLight
                    SideEffect {
                        systemUiController.setStatusBarColor(
                            color = systemColor,
                            darkIcons = isLight
                        )
                    }

                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "index"
                    ) {
                        // 主页
                        composable("index") {
                            Index(navController, diaryViewModel)
                        }

                        // 日记编辑页面
                        composable("edit") {
                            EditPage(navController, diaryViewModel)
                        }

                        // 关于页面
                        composable("about"){
                            AboutPage(navController)
                        }
                    }
                }
            }
        }
    }
}