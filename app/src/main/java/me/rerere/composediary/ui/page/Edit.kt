package me.rerere.composediary.ui.page

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import me.rerere.composediary.DiaryViewModel
import me.rerere.composediary.R
import me.rerere.composediary.util.formatAsTime

@Composable
fun EditPage(navController: NavController, diaryViewModel: DiaryViewModel) {
    val state = diaryViewModel.currentEditing
    var content by rememberSaveable { mutableStateOf("") }
    var date by remember { mutableStateOf(0L) }
    // 载入完成后更新content
    LaunchedEffect(state) {
        content = state.content
        date = state.date
    }


    // context
    val context = LocalContext.current

    val canNotEmpty = stringResource(R.string.edit_save_notempty)
    val saved = stringResource(R.string.edit_save_done)
    EditUI(content, date, state.id, {
        if (content.isEmpty()) {
            Toast.makeText(context, canNotEmpty, Toast.LENGTH_SHORT).show()
        } else {
            state.content = content
            diaryViewModel.update(state)
            Toast.makeText(context, saved, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }, {
        content = it
        state.content = content
    })
}

@Composable
fun EditUI(content: String, date: Long, id: Int, onSave: () -> Unit, onChange: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_title, date.formatAsTime())) },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.Save, "Save")
                    }
                }
            )
        }
    ) {
        // 日记编辑框
        BasicTextField(
            value = content,
            onValueChange = onChange,
            Modifier.fillMaxSize(),
            textStyle = LocalTextStyle.current.copy(
                // 调整字号
                fontSize = 25.sp
            )
        )
    }
}