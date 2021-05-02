package me.rererecomposediary.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.rererecomposediary.ComposeDiaryApp
import me.rererecomposediary.DiaryViewModel
import me.rererecomposediary.DiaryViewModelFactory

@Composable
fun EditPage(navController: NavController, id: Int?) {
    val diaryViewModel =
        viewModel<DiaryViewModel>(factory = DiaryViewModelFactory(ComposeDiaryApp.repo))

    if (id == null) {
        Text(text = "肥肠抱歉，无法获取日记ID！")
        return
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "编辑日记")}
            )
        }
    ) {
        var content by remember {
            mutableStateOf("")
        }
        BasicTextField(value = content, onValueChange = { content = it }, Modifier.fillMaxSize())
    }
}