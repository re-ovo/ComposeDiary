package me.rerere.composediary.ui.page

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import me.rerere.composediary.R

// 头像地址 0w0
// Avatar URL
private const val AVATAR_URL = "https://avatars.githubusercontent.com/u/21152113"

@Composable
fun AboutPage(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(R.string.about_title))
            }, navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.ArrowBack, "Back To Index")
                }
            })
        }
    ) {
        // 页面内容
        // Page Content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 作者信息
            // Author Info
            Card(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(120.dp),
                elevation = 4.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 头像
                        val avatarSource = rememberCoilPainter(request = AVATAR_URL, fadeIn = true)
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(90.dp)
                        ) {
                            Image(
                                painter = avatarSource,
                                "Avatar"
                            )
                        }

                        // 网名/Github
                        val context = LocalContext.current
                        Column(Modifier.clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/jiangdashao")
                            )
                            context.startActivity(intent)
                        }) {
                            Text(text = "RE_OVO", style = MaterialTheme.typography.h4)
                            Text(text = "https://github.com/jiangdashao", color = Color.Blue)
                        }
                    }
                }
            }

            val context = LocalContext.current

            // 联系方式
            // Contact Info
            Text(
                text = stringResource(R.string.about_items_contact),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Tencent QQ
            ContactInfo(icon = {
                Image(
                    painter = painterResource(id = R.drawable.qq),
                    contentDescription = "QQ",
                    modifier = Modifier.size(30.dp)
                )
            }, text = "QQ: 1609403959")
            // Boss直聘
            ContactInfo(icon = {
                Image(
                    painter = painterResource(id = R.drawable.boss),
                    contentDescription = "BossZP",
                    modifier = Modifier.size(40.dp)
                )
            }, text = "Boss直聘") {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://m.zhipin.com/mpa/html/resume-detail?sid=self&securityId=1IPxzNwoQo4sE-W18WAog3HjhwPqVR45Wuuv9jeSBo2OFq1h3z2x0G1QMev4Uhzdp1XYuUtNsoPOJeNEjk9Yk4Jt-p1-tB1rXHnIWgec7qQsBbDzJob0gCyv8hmUtYebUCOm1fbQzpO-K_fKXxTgIxo5xAkf5elIpOkKxw~~\n")
                )
                context.startActivity(intent)
            }

            // 所用开源库
            // Third-party library
            Text(
                text = stringResource(R.string.about_opensource_libs),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            OpenSourceLibraryItem(
                name = "Accompanist",
                link = "https://github.com/google/accompanist"
            )

            // Donate
            Text(
                text = stringResource(R.string.about_donate), modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp), textAlign = TextAlign.Center
            )

            if (Locale.current.language == "zh") {
                // Chinese users, show alipay QR code
                Card(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.alipay),
                            contentDescription = "恰饭"
                        )
                        Text("蟹蟹")
                    }
                }
            } else {
                // show paypal address
                Card(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("https://paypal.me/matrixac")
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInfo(icon: @Composable () -> Unit, text: String, clickHandler: () -> Unit = {}) {
    Card(
        Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(45.dp)
            .clickable { clickHandler() },
        elevation = 2.dp
    ) {
        Row(Modifier.padding(8.dp)) {
            Box(modifier = Modifier.clip(CircleShape)) {
                icon()
            }
            Text(text)
        }
    }
}

@Composable
fun OpenSourceLibraryItem(name: String, link: String) {
    val context = LocalContext.current

    Card(
        Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(link)
                )
                context.startActivity(intent)
            },
        elevation = 2.dp
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(text = name, fontStyle = FontStyle.Italic)
            Text(text = link, color = Color.Blue)
        }
    }
}