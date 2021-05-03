package me.rerere.composediary.ui.page

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.TimeUtils
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.rerere.composediary.ComposeDiaryApp
import me.rerere.composediary.DiaryViewModel
import me.rerere.composediary.DiaryViewModelFactory
import me.rerere.composediary.R
import me.rerere.composediary.model.Diary
import me.rerere.composediary.util.formatAsTime
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalFoundationApi
@Composable
fun Index(navController: NavController, diaryViewModel: DiaryViewModel) {
    val diaryList: List<Diary> by diaryViewModel.diaryList.observeAsState(emptyList())
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    Row {

                        /* 测试按钮，用于批量创建测试数据

                        IconButton(onClick = {
                            repeat(10) {
                                diaryViewModel.insert(
                                    Diary(
                                        0,
                                        "哈哈哈哈",
                                        System.currentTimeMillis() - (TimeUnit.DAYS.toMillis(it.toLong()))
                                    )
                                )
                            }
                        }) {
                            Icon(
                                Icons.Default.Add, "Test data"
                            )
                        }
                         */

                        // 清空所有日记
                        IconButton(onClick = {
                            diaryViewModel.deleteAll()
                        }) {
                            Icon(
                                Icons.Default.DeleteForever, "Delete All"
                            )
                        }

                        // 搜索日记
                        IconButton(onClick = {
                            // Toast.makeText(context, "暂时不支持搜索功能！", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Search, "Search a diary")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                diaryViewModel.startEditing(-1)// -1 = create a new diary
                navController.navigate("edit")
            }) {
                Icon(Icons.Rounded.Add, "Create a diary")
            }
        }
    ) {
        val expandIndex: MutableState<Int> = remember {
            mutableStateOf(0)
        }
        if(diaryList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "点击 \"+\" 新建日记")
            }
        } else {
            val grouped = diaryList.groupBy { it.date.formatAsTime() }
            LazyColumn {
                grouped.forEach {
                    val (date, daries) = it
                    stickyHeader {
                        Surface(
                            Modifier
                                .padding(start = 10.dp)
                                .fillMaxWidth()
                        ) {
                            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                                Text(
                                    text = date,
                                    style = TextStyle.Default.copy(
                                        color = Color(0xff5ab9e8),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                    items(daries) { diary ->
                        DiaryCard(
                            diary,
                            diaryViewModel,
                            navController,
                            expandIndex,
                            scaffoldState,
                            scope
                        )
                    }
                }
                /*
            items(diaryList) { diary ->
                DiaryCard(diary, diaryViewModel, navController, expandIndex, scaffoldState, scope)
            }*
             */
            }
        }
    }
}

@Composable
fun DiaryCard(
    diary: Diary,
    diaryViewModel: DiaryViewModel,
    navController: NavController,
    expandIndex: MutableState<Int>,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope
) {
    var expand by remember { mutableStateOf(false) }
    // val context = LocalContext.current

    // 实现展开互斥操作
    LaunchedEffect(expandIndex.value) {
        if (expand && expandIndex.value != diary.id) {
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
                if (expand) {
                    expandIndex.value = diary.id
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(diary.content)
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
                    Text(text = "日期: ${diary.date.formatAsTime()} ID: ${diary.id}")
                }
                if (expand) {
                    Row {
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            diaryViewModel.startEditing(diary.id)
                            navController.navigate("edit?id=${diary.id}")
                        }) {
                            Icon(Icons.Default.Edit, "Edit the diary")
                        }
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            diaryViewModel.delete(diary)
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    "已删除日记",
                                    "关闭",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) {
                            Icon(Icons.Default.Delete, "Delete the diary")
                        }
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            val clipboard: ClipboardManager =
                                ComposeDiaryApp.appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(null, diary.content))
                            // Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("已复制到剪贴板", "关闭")
                            }
                        }) {
                            Icon(Icons.Default.CopyAll, "Copy the diary")
                        }
                    }
                }
            }
        }
    }
}