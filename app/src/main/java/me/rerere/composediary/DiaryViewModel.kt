package me.rerere.composediary

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import me.rerere.composediary.model.Diary
import me.rerere.composediary.repo.DiaryRepo

class DiaryViewModel(private val diaryRepo: DiaryRepo) : ViewModel() {
    // 日记列表
    val diaryList : LiveData<List<Diary>> = diaryRepo.allDiary.asLiveData()

    // 当前正在编辑的日记
    var currentEditing = mutableStateOf(Diary(-1,""))

    fun insert(diary: Diary) = viewModelScope.launch {
        diaryRepo.insertDiary(diary)
    }

    fun update(diary: Diary) = viewModelScope.launch {
        diaryRepo.update(diary)
    }

    fun delete(diary: Diary) = viewModelScope.launch {
        diaryRepo.delete(diary)
    }

    fun startEditing(id: Int?){
        if(id == null){
            currentEditing.value = Diary(0,"")
            insert(currentEditing.value)
        }else {
            viewModelScope.launch {
                currentEditing.value = diaryRepo.getDiaryById(id)
            }
        }
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