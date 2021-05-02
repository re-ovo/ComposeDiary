package me.rerere.composediary.ui.page

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.google.accompanist.insets.statusBarsPadding
import me.rerere.composediary.ComposeDiaryApp
import me.rerere.composediary.DiaryViewModel
import me.rerere.composediary.DiaryViewModelFactory
import me.rerere.composediary.R
import me.rerere.composediary.model.Diary
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
                        IconButton(onClick = {
                            diaryViewModel.deleteAll()
                        }) {
                            Icon(
                                Icons.Default.DeleteForever, "Delete All"
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate("edit")
                        }) {
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
        val expandIndex: MutableState<Int> = remember {
            mutableStateOf(0)
        }
        LazyColumn {
            items(diaryList) { diary ->
                DiaryCard(diary, diaryViewModel, navController, expandIndex )
            }
        }
    }
}

@Composable
fun DiaryCard(diary: Diary, diaryViewModel: DiaryViewModel, navController: NavController, expandIndex: MutableState<Int>) {
    var expand by remember { mutableStateOf(false) }

    // 实现展开互斥操作
    LaunchedEffect(expandIndex.value){
        if(expand && expandIndex.value != diary.id){
            expand = false
        }
    }

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .animateContentSize()
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                expand = !expand
                if(expand) {
                    expandIndex.value = diary.id
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(diary.content)
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(text = "日期: ${DateFormat.getDateInstance().format(Date(diary.date))}")
                }
                if (expand) {
                    Row {
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            navController.navigate("edit?id=${diary.id}")
                        }) {
                            Icon(Icons.Default.Edit, "Edit the diary")
                        }
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = { diaryViewModel.delete(diary) }) {
                            Icon(Icons.Default.Delete, "Delete the diary")
                        }
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            val clipboard: ClipboardManager =
                                ComposeDiaryApp.appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(null, diary.content))
                        }) {
                            Icon(Icons.Default.CopyAll, "Copy the diary")
                        }
                    }
                }
            }
        }
    }
}