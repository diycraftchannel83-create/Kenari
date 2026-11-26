package com.winsmediatama.kenari.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KenariViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, KenariDatabase::class.java, "kenari-tasik.db").build()
    private val repository = BirdRepository(db.birdDao())
    val birds = repository.birds.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun save(bird: BirdEntity, onResult: (Result<Long>) -> Unit) {
        viewModelScope.launch { onResult(repository.save(bird)) }
    }

    fun delete(bird: BirdEntity) {
        viewModelScope.launch { repository.delete(bird) }
    }

    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}
