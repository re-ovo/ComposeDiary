package me.rerere.composediary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Diary(
    // 日记ID
    // Diary ID
    @PrimaryKey(autoGenerate = true) var id: Int,

    // 日记内容
    // Diary Content
    @ColumnInfo(name = "content") var content: String,

    // 日记日期
    // Diary Creation Date
    @ColumnInfo(name = "date") var date: Long = System.currentTimeMillis()
)