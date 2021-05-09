package me.rerere.composediary

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.rerere.composediary.model.Diary
import me.rerere.composediary.repo.DiaryRepo

class DiaryViewModel(private val diaryRepo: DiaryRepo) : ViewModel() {
    init {
        viewModelScope.launch {
            val sharedPreferences = withContext(Dispatchers.IO){
                ComposeDiaryApp.appContext.getSharedPreferences("diarySetting", Context.MODE_PRIVATE)
            }
            followSystemDarkMode = sharedPreferences.getBoolean("followSystemDarkMode", true)
        }
    }

    // 日记列表
    val diaryList: LiveData<List<Diary>> = diaryRepo.allDiary.asLiveData()

    // 搜索结果
    var searchMode by mutableStateOf(false)
    private val _searchingResult = MutableLiveData<List<Diary>>()
    val searchingResult: LiveData<List<Diary>> = _searchingResult

    // 当前正在编辑的日记
    var currentEditing by mutableStateOf(Diary(0, ""))

    // 跟随系统暗色模式
    var followSystemDarkMode by mutableStateOf(true)

    fun search(content: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = diaryRepo.search("%$content%")
        withContext(Dispatchers.Main){
            _searchingResult.value = result
        }
    }

    fun insert(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.insertDiary(diary)
    }

    fun update(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.update(diary)
    }

    fun delete(diary: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.delete(diary)
    }

    fun updateSetting() = viewModelScope.launch(Dispatchers.IO) {
        val sharedPreferences = ComposeDiaryApp.appContext.getSharedPreferences("diarySetting", Context.MODE_PRIVATE)
        sharedPreferences.edit(commit = true){
            putBoolean("followSystemDarkMode", followSystemDarkMode)
        }
    }

    fun startEditing(id: Int) {
        when (id) {
            // -1: 新建日记
            -1 -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val diary = Diary(0,"")
                    diary.id = diaryRepo.insertDiary(diary).toInt()
                    withContext(Dispatchers.Main){
                        currentEditing = diary
                    }
                }
            }
            // 打开已有日记
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val diary = diaryRepo.getDiaryById(id)
                    withContext(Dispatchers.Main){
                        currentEditing = diary
                    }
                }
            }
        }
    }


    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        diaryRepo.deleteAll()
    }
}

object DiaryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            return DiaryViewModel(ComposeDiaryApp.repo) as T
        }
        error("Unknown view model class: ${modelClass.name}")
    }
}