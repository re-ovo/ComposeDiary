package me.rererecomposediary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import me.rererecomposediary.ui.Index
import me.rererecomposediary.ui.page.EditPage
import me.rererecomposediary.ui.theme.ComposeDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDiaryTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "index"){
                    // 主页
                    composable("index"){
                        Index(navController)
                    }

                    // 日记编辑页面
                    composable("edit/{id}", arguments = listOf(navArgument("id"){
                        type = NavType.IntType
                    })){
                        EditPage(navController, it.arguments?.getInt("id"))
                    }
                }
            }
        }
    }
}