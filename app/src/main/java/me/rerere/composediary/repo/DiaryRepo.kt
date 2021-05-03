package me.rerere.composediary.repo

import kotlinx.coroutines.flow.Flow
import me.rerere.composediary.dao.DiaryDao
import me.rerere.composediary.model.Diary

class DiaryRepo(private val diaryDao: DiaryDao) {
    val allDiary: Flow<List<Diary>> = diaryDao.getAll()

    suspend fun getDiaryById(id: Int): Diary = diaryDao.getDiaryByID(id)

    suspend fun insertDiary(diary: Diary): Long = diaryDao.insert(diary)

    suspend fun update(diary: Diary) = diaryDao.update(diary)

    suspend fun delete(diary: Diary) = diaryDao.delete(diary)

    suspend fun deleteAll() = diaryDao.deleteAll()
}