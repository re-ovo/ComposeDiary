package me.rerere.composediary.ui.page

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import me.rerere.composediary.ComposeDiaryApp
import me.rerere.composediary.DiaryViewModel
import me.rerere.composediary.DiaryViewModelFactory

@Composable
fun EditPage(navController: NavController, id: Int) {
    val diaryViewModel = viewModel<DiaryViewModel>(factory = DiaryViewModelFactory(ComposeDiaryApp.repo))

    // Load
    diaryViewModel.startEditing(id)

    val state = diaryViewModel.currentEditing
    var content by remember { mutableStateOf("") }
    // 载入完成后更新content
    LaunchedEffect(state) {
        content = state.content
    }

    // context
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "编辑日记") },
                actions = {
                    IconButton(onClick = {
                        state.content = content
                        diaryViewModel.update(state)
                        Toast.makeText(context, "保存完成!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Save, "Save")
                    }
                }
            )
        }
    ) {
        BasicTextField(value = content, onValueChange = { content = it }, Modifier.fillMaxSize())
    }
}