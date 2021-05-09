package me.rerere.composediary.ui.page

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.TimeUtils
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
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
import soup.compose.material.motion.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalMaterialApi
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
            TopBar(diaryViewModel, scaffoldState)
        },

        // Drawer
        drawerContent = {
            Drawer(navController, diaryViewModel)
        },

        // FAB, 用于新建日记
        floatingActionButton = {
            FloatingActionButton(onClick = {
                diaryViewModel.startEditing(-1)// -1 = create a new diary
                navController.navigate("edit")
            }) {
                Icon(Icons.Rounded.Add, "Create a diary")
            }
        }
    ) {
        // 当前正在展开的Item, 主要用于互斥展开(同时仅能有一个item被展开)
        val expandIndex: MutableState<Int> = remember {
            mutableStateOf(0)
        }
        // 展示日记列表
        if (diaryList.isEmpty() && !diaryViewModel.searchMode) {
            // 没有日记
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "点击 \"+\" 新建日记")
            }
        } else {
            val searchResult by diaryViewModel.searchingResult.observeAsState(emptyList())

            // 有日记，通过LazyColumn显示
            val grouped =
                if (!diaryViewModel.searchMode) diaryList.groupBy { it.date.formatAsTime() } else searchResult.groupBy { it.date.formatAsTime() }  // 合并同一天的日记

            Column {
                // 仅在搜索模式下展示
                if (diaryViewModel.searchMode) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "搜索结果: (${searchResult.size}条记录)"
                    )
                }

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
}

@ExperimentalMaterialApi
@Composable
fun Drawer(navController: NavController, diaryViewModel: DiaryViewModel) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header, items, bottom) = createRefs()

        // Header
        Surface(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(130.dp)
                .constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            color = MaterialTheme.colors.secondary,
            elevation = 4.dp,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Row {
                    Box(modifier = Modifier.clip(CircleShape)) {
                        Image(
                            painterResource(id = R.drawable.logo),
                            "Jetpack Compose Logo",
                            Modifier.size(90.dp)
                        )
                    }
                    Column(
                        Modifier
                            .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "ComposeDiary", style = MaterialTheme.typography.h5)
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(text = "Based on Jetpack Compose™")
                        }
                    }
                }
            }
        }
        // Items
        val context = LocalContext.current
        Column(modifier = Modifier.constrainAs(items) {
            top.linkTo(header.bottom)
            start.linkTo(parent.start)
        }) {
            //github
            ListItem(
                icon = { Icon(Icons.Default.Source, "Source code") },
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/jiangdashao/ComposeDiary")
                    )
                    context.startActivity(intent)
                }) {
                Text(text = "源代码")
            }
            //about
            ListItem(
                icon = { Icon(Icons.Default.Info, "About the App") },
                modifier = Modifier.clickable {
                    navController.navigate("about")
                }) {
                Text(text = "关于")
            }
        }

        // Switch
        Card(
            modifier = Modifier
                .constrainAs(bottom) {
                    this.bottom.linkTo(parent.bottom)
                    this.start.linkTo(parent.start)
                }
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(Modifier.padding(16.dp)) {
                Text(text = "跟随系统暗色模式")
                Switch(checked = diaryViewModel.followSystemDarkMode, onCheckedChange = {
                    diaryViewModel.followSystemDarkMode = it
                    diaryViewModel.updateSetting()
                })
            }
        }
    }
}

@Composable
fun TopBar(diaryViewModel: DiaryViewModel, scaffoldState: ScaffoldState) {
    val scope = rememberCoroutineScope()
    // 切换动画
    MaterialMotion(targetState = diaryViewModel.searchMode, motionSpec = crossfade()) {
        if (it) {
            TopAppBar {
                var searchContent by remember {
                    mutableStateOf("")
                }
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    // 搜索框
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            value = searchContent,
                            onValueChange = { newValue ->
                                searchContent = newValue
                                if (newValue.isNotBlank()) {
                                    diaryViewModel.search(newValue)
                                }
                            },
                            singleLine = true,
                            textStyle = TextStyle.Default.copy(fontSize = 16.sp)
                        )
                    }

                    // 关闭搜索功能
                    IconButton(modifier = Modifier.wrapContentWidth(), onClick = {
                        diaryViewModel.searchMode = false
                    }) {
                        Icon(Icons.Default.Close, "Close Search Bar")
                    }
                }
            }
        } else {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, "Navigation")
                    }
                },
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
                            diaryViewModel.searchMode = true
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