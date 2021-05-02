package me.rerere.composediary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.rerere.composediary.dao.DiaryDao
import me.rerere.composediary.model.Diary

@Database(entities = [Diary::class], version = 1, exportSchema = false)
abstract class DiaryDB : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}