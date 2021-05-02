package me.rererecomposediary.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import me.rererecomposediary.ComposeDiaryApp
import me.rererecomposediary.DiaryViewModel
import me.rererecomposediary.DiaryViewModelFactory
import me.rererecomposediary.R
import me.rererecomposediary.model.Diary
import java.text.DateFormat
import java.util.*

@Composable
fun Index(navController: NavController) {
    val diaryViewModel =
        viewModel<DiaryViewModel>(factory = DiaryViewModelFactory(ComposeDiaryApp.repo))
    val diaryList: List<Diary> by diaryViewModel.diaryList.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    Row {
                        IconButton(onClick = { diaryViewModel.insert(Diary(0, "你写个锤子的日记")) }) {
                            Icon(
                                Icons.Default.Add, "Create a diary"
                            )
                        }
                        IconButton(onClick = {
                            // Toast.makeText(context, "暂时不支持搜索功能！", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Search, "Search a diary")
                        }
                    }
                }
            )
        }
    ) {
        LazyColumn {
            items(diaryList) { diary ->
                DiaryCard(diary, diaryViewModel, navController)
            }
        }
    }
}

@Composable
fun DiaryCard(diary: Diary, diaryViewModel: DiaryViewModel, navController: NavController) {
    var expand by remember { mutableStateOf(false) }
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .animateContentSize()
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { expand = !expand }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var content by remember {
                mutableStateOf(diary.content)
            }
            Text(content)
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(text = "日期: ${DateFormat.getDateInstance().format(Date(diary.date))}")
                }
                if (expand) {
                    Row {
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            navController.navigate("edit/${diary.id}")
                        }) {
                            Icon(Icons.Default.Edit,"Edit the diary")
                        }
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = { diaryViewModel.delete(diary)}) {
                            Icon(Icons.Default.Delete,"Delete the diary")
                        }
                    }
                }
            }
        }
    }
}