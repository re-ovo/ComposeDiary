package me.rererecomposediary.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Diary(
    // 日记ID
    @PrimaryKey(autoGenerate = true) var id: Int,

    // 日记内容
    @ColumnInfo(name = "content") var content: String,

    // 日记日期
    @ColumnInfo(name = "date") var date: Long = System.currentTimeMillis()
)