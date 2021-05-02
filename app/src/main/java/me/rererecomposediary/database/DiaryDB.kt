package me.rererecomposediary.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.rererecomposediary.dao.DiaryDao
import me.rererecomposediary.model.Diary

@Database(entities = [Diary::class], version = 1, exportSchema = false)
abstract class DiaryDB : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}