package me.rerere.composediary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.rerere.composediary.model.Diary
import me.rerere.composediary.repo.DiaryRepo

class DiaryViewModel(private val diaryRepo: DiaryRepo) : ViewModel() {
    // 日记列表
    val diaryList: LiveData<List<Diary>> = diaryRepo.allDiary.asLiveData()

    // 当前正在编辑的日记
    var currentEditing by mutableStateOf(Diary(-1, ""))


    fun insert(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.insertDiary(diary)
    }

    fun update(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.update(diary)
    }

    fun delete(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.delete(diary)
    }

    fun startEditing(id: Int) {
        when (id) {
            // -1: 新建日记
            -1 -> {
                val diary = Diary(0, "")
                insert(diary)
                currentEditing = diary
            }
            // 打开已有日记
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    currentEditing = diaryRepo.getDiaryById(id)
                }
            }
        }
    }


    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.deleteAll()
    }
}

class DiaryViewModelFactory(private val diaryRepo: DiaryRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            return DiaryViewModel(diaryRepo) as T
        }
        error("Unknown view model class: ${modelClass.name}")
    }
}