package me.rererecomposediary

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import me.rererecomposediary.model.Diary
import me.rererecomposediary.repo.DiaryRepo

class DiaryViewModel(private val diaryRepo: DiaryRepo) : ViewModel() {
    val diaryList : LiveData<List<Diary>> = diaryRepo.allDiary.asLiveData()

    fun insert(diary: Diary) = viewModelScope.launch {
        diaryRepo.insertDiary(diary)
    }

    fun update(diary: Diary) = viewModelScope.launch {
        diaryRepo.update(diary)
    }

    fun delete(diary: Diary) = viewModelScope.launch {
        diaryRepo.delete(diary)
    }
}

class DiaryViewModelFactory(private val diaryRepo: DiaryRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DiaryViewModel::class.java)){
            return DiaryViewModel(diaryRepo) as T
        }
        error("Unknown view model class: ${modelClass.name}")
    }
}