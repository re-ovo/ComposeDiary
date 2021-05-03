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
import soup.compose.material.motion.MaterialMotion
import soup.compose.material.motion.materialElevationScale
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

        // 支持搜索功能的顶栏
        topBar = {
            TopBar(diaryViewModel)
        },

        // FAB, 用于新建日记
        floatingActionButton = {
            ExtendedFloatingActionButton(text = {
                Text(text = "新建")
            }, onClick = {
                diaryViewModel.startEditing(-1)// -1 = create a new diary
                navController.navigate("edit")
            }, icon = {
                Icon(Icons.Rounded.Add, "Create a diary")
            })
        }
    ) {
        // 当前正在展开的Item, 主要用于互斥展开(同时仅能有一个item被展开)
        val expandIndex: MutableState<Int> = remember {
            mutableStateOf(0)
        }
        // 展示日记列表
        if (diaryList.isEmpty()) {
            // 没有日记
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "点击 \"+\" 新建日记")
            }
        } else {
            // 有日记，通过LazyColumn显示
            val grouped = diaryList.groupBy { it.date.formatAsTime() }// 合并同一天的日记
            LazyColumn {
                grouped.forEach {
                    val (date, dairies) = it

                    // 显示日期sticker (实验性API)
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

                    // 显示当前日期下的日记item
                    items(dairies) { diary ->
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
            }
        }
    }
}

@Composable
fun TopBar(diaryViewModel: DiaryViewModel) {
    var searchMode by remember {
        mutableStateOf(false)
    }
    // 切换动画
    MaterialMotion(targetState = searchMode, motionSpec = materialElevationScale(growing = true)) {
        if (it) {
            TopAppBar {
                Row {
                    var searchContent by remember { mutableStateOf("") }
                    TextField(value = searchContent, onValueChange = {
                        searchContent = it
                    })
                    IconButton(onClick = {
                        searchContent = ""
                        searchMode = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close search bar")
                    }
                }
            }
        } else {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    Row {
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
                            searchMode = true
                        }) {
                            Icon(Icons.Default.Search, "Search a diary")
                        }
                    }
                }
            )
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
    // 是否展开这个卡片，显示各种操作icon
    var expand by remember { mutableStateOf(false) }

    // 实现展开互斥
    LaunchedEffect(expandIndex.value) {
        if (expand && expandIndex.value != diary.id) {
            expand = false
        }
    }

    // 日记信息卡片
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
            Column {
                // 显示日记内容
                Text(diary.content)

                // 展开操作图标
                if (expand) {
                    Row {
                        // 编辑日记
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            diaryViewModel.startEditing(diary.id)
                            navController.navigate("edit?id=${diary.id}")
                        }) {
                            Icon(Icons.Default.Edit, "Edit the diary")
                        }

                        // 删除日记
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

                        // 复制日记内容
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