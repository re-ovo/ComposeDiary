package me.rerere.composediary.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun Preview(){
    RoundedButton(icon = { Icon(Icons.Default.Delete, "") }, text = "Delete ALL") {

    }
}

@Composable
fun RoundedButton(icon: @Composable ()-> Unit, text: String, onClick: ()->Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colors.secondary
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 5.dp)) {
            icon()
            Text(text)
        }
    }
}