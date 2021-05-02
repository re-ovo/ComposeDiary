package me.rererecomposediary

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import me.rererecomposediary.dao.DiaryDao
import me.rererecomposediary.database.DiaryDB
import me.rererecomposediary.repo.DiaryRepo

class ComposeDiaryApp : Application() {
    companion object {
        lateinit var appContext: ComposeDiaryApp

        val database: DiaryDB by lazy {
            Room.databaseBuilder(appContext, DiaryDB::class.java, "diary_database").build()
        }

        val repo: DiaryRepo by lazy {
            DiaryRepo(database.diaryDao())
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}