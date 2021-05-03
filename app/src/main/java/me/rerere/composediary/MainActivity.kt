package me.rerere.composediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.transition.MaterialSharedAxis
import me.rerere.composediary.ui.page.EditPage
import me.rerere.composediary.ui.page.Index
import me.rerere.composediary.ui.theme.ComposeDiaryTheme
import soup.compose.material.motion.Axis
import soup.compose.material.motion.MaterialMotion
import soup.compose.material.motion.materialSharedAxis

class MainActivity : ComponentActivity() {
    private val diaryViewModel by viewModels<DiaryViewModel>(factoryProducer = {
        DiaryViewModelFactory(ComposeDiaryApp.repo)
    })

    @ExperimentalFoundationApi
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
                            Index(navController, diaryViewModel)
                        }

                        // 日记编辑页面
                        composable("edit") {
                            EditPage(navController, diaryViewModel)
                        }
                    }
                }
            }
        }
    }
}