package me.rerere.composediary

import android.app.Application
import androidx.room.Room
import me.rerere.composediary.database.DiaryDB
import me.rerere.composediary.repo.DiaryRepo

class ComposeDiaryApp : Application() {
    companion object {
        lateinit var appContext: ComposeDiaryApp

        private val database: DiaryDB by lazy {
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