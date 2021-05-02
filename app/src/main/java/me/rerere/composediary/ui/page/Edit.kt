package me.rerere.composediary.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.rerere.composediary.ComposeDiaryApp
import me.rerere.composediary.DiaryViewModel
import me.rerere.composediary.DiaryViewModelFactory
import me.rerere.composediary.model.Diary

@Composable
fun EditPage(navController: NavController, id: Int?) {
    val diaryViewModel =
        viewModel<DiaryViewModel>(factory = DiaryViewModelFactory(ComposeDiaryApp.repo))

    val diary: Diary = if(id == null){
        // TODO
        // 无ID传入，创建新日记
        Diary(0,"TODO")
    } else {
        // 读取日记
        // TODO
        Diary(0,"TODO")
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