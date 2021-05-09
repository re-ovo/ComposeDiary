package me.rerere.composediary.ui.page

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.rerere.composediary.ComposeDiaryApp
import me.rerere.composediary.DiaryViewModel
import me.rerere.composediary.R
import me.rerere.composediary.model.Diary
import me.rerere.composediary.util.formatAsTime

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
        // A topbar with search feature
        topBar = {
            TopBar(diaryViewModel, scaffoldState)
        },

        // Drawer
        drawerContent = {
            Drawer(navController, diaryViewModel)
        },

        // FAB, 用于新建日记
        // FAB, create diary
        floatingActionButton = {
            FloatingActionButton(onClick = {
                diaryViewModel.startEditing(-1)// -1 = create a new diary
                navController.navigate("edit")
            }) {
                Icon(Icons.Default.Create, stringResource(R.string.add_diary_description))
            }
        }
    ) {
        // Current expanded item id (Prevent multiple items from being expanded at the same time)
        // 当前正在展开的Item, 主要用于互斥展开(同时仅能有一个item被展开)
        val expandIndex: MutableState<Int> = remember {
            mutableStateOf(0)
        }
        // Show diary list
        // 展示日记列表
        if (diaryList.isEmpty() && !diaryViewModel.searchMode) {
            // There is no diary, show creation helper
            // 没有日记, 显示创建日记帮助
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.empty_diary))
            }
        } else {
            val searchResult by diaryViewModel.searchingResult.observeAsState(emptyList())

            // Group the diary by their date
            val grouped =
                if (!diaryViewModel.searchMode) diaryList.groupBy { it.date.formatAsTime() } else searchResult.groupBy { it.date.formatAsTime() }  // 合并同一天的日记

            Column {
                // Only show in search mode
                // 仅在搜索模式下展示
                if (diaryViewModel.searchMode) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.search_result, searchResult.size)
                    )
                }

                // There is diaries, show in the lazy column
                // 有日记，通过LazyColumn显示
                LazyColumn {
                    grouped.forEach {
                        val (date, dairies) = it

                        // Show diary date in sticker (experimental)
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

                        // Show all diaries of the date
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
                            stringResource(R.string.drawer_logo_description),
                            Modifier.size(90.dp)
                        )
                    }
                    Column(
                        Modifier
                            .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.h5)
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                            Text(stringResource(R.string.drawer_subtitle))
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
                icon = {
                    Icon(
                        Icons.Default.Source,
                        stringResource(R.string.drawer_item_sourcecode)
                    )
                },
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/jiangdashao/ComposeDiary")
                    )
                    context.startActivity(intent)
                }) {
                Text(stringResource(R.string.drawer_item_sourcecode))
            }
            //about
            ListItem(
                icon = { Icon(Icons.Default.Info, stringResource(R.string.drawer_item_aboutapp)) },
                modifier = Modifier.clickable {
                    navController.navigate("about")
                }) {
                Text(text = stringResource(R.string.drawer_item_aboutapp))
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
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(Modifier.padding(8.dp)) {
                Icon(Icons.Default.DarkMode, stringResource(R.string.drawer_bottom_followdarkmode))
                Text(
                    text = stringResource(R.string.drawer_bottom_followdarkmode),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
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
    // Search bar animation
    Crossfade(targetState = diaryViewModel.searchMode, animationSpec = tween(durationMillis = 400)) {
        if (it) {
            // Search Bar
            TopAppBar {
                var searchContent by remember {
                    mutableStateOf("")
                }
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    // input field
                    // 搜索框
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray),
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
                    // Close search bar
                    IconButton(modifier = Modifier.wrapContentWidth(), onClick = {
                        diaryViewModel.searchMode = false
                    }) {
                        Icon(Icons.Default.Close, stringResource(R.string.close_searchbar))
                    }
                }
            }
        } else {
            // Normal Bar
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, stringResource(R.string.topbar_drawer_description))
                    }
                },
                actions = {
                    Row {
                        // Clear all diaries
                        // 清空所有日记
                        IconButton(onClick = {
                            diaryViewModel.deleteAll()
                        }) {
                            Icon(
                                Icons.Default.DeleteForever,
                                stringResource(R.string.topbar_drawer_deleteall)
                            )
                        }

                        // Search diary
                        // 搜索日记
                        IconButton(onClick = {
                            diaryViewModel.searchMode = true
                        }) {
                            Icon(
                                Icons.Default.Search,
                                stringResource(R.string.topbar_drawer_search)
                            )
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
    // Whether to expand this card to show buttons
    // 是否展开这个卡片，显示各种操作icon
    var expand by remember { mutableStateOf(false) }

    // Make sure only one card expands at the same time
    // 实现展开互斥
    LaunchedEffect(expandIndex.value) {
        if (expand && expandIndex.value != diary.id) {
            expand = false
        }
    }

    // Diary Card
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 30.dp)
                ) {
                    if(expand){
                        // able to select when expanded
                        SelectionContainer {
                            Text(diary.content)
                        }
                    }else {
                        Text(diary.content)
                    }
                }

                // 展开操作图标
                if (expand) {
                    Row {
                        // Edit the diary
                        // 编辑日记
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            diaryViewModel.startEditing(diary.id)
                            navController.navigate("edit?id=${diary.id}")
                        }) {
                            Icon(Icons.Default.Edit, stringResource(R.string.diaryitem_edit_description))
                        }

                        // Delete the diary
                        // 删除日记
                        val text = stringResource(R.string.delete_diary_snackbar_text)
                        val actionLabel = stringResource(R.string.delete_diary_snackbar_label)
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            diaryViewModel.delete(diary)
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    text,
                                    actionLabel,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) {
                            Icon(Icons.Default.Delete, stringResource(R.string.diaryitem_delete_description))
                        }

                        // Copy diary
                        // 复制日记内容
                        val copied = stringResource(R.string.diaryitem_copy_done)
                        IconButton(modifier = Modifier
                            .padding(8.dp)
                            .size(25.dp), onClick = {
                            val clipboard: ClipboardManager =
                                ComposeDiaryApp.appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(null, diary.content))
                            // Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(copied)
                            }
                        }) {
                            Icon(Icons.Default.CopyAll, stringResource(R.string.diaryitem_copy_description))
                        }
                    }
                }
            }
        }
    }
}