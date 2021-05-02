package me.rererecomposediary.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import me.rererecomposediary.model.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary")
    fun getAll() : Flow<List<Diary>>

    @Query("SELECT * FROM diary WHERE id=:id LIMIT 1")
    suspend fun getDiaryByID(id: Int): Diary

    @Insert
    suspend fun insert(diary: Diary)

    @Update
    suspend fun update(diary: Diary)

    @Delete
    suspend fun delete(diary: Diary)
}