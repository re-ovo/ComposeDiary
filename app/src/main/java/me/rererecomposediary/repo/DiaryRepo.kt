package me.rererecomposediary.repo

import kotlinx.coroutines.flow.Flow
import me.rererecomposediary.dao.DiaryDao
import me.rererecomposediary.model.Diary

class DiaryRepo(private val diaryDao: DiaryDao) {
    val allDiary: Flow<List<Diary>> = diaryDao.getAll()

    suspend fun insertDiary(diary: Diary) = diaryDao.insert(diary)

    suspend fun update(diary: Diary) = diaryDao.update(diary)

    suspend fun delete(diary: Diary) = diaryDao.delete(diary)
}