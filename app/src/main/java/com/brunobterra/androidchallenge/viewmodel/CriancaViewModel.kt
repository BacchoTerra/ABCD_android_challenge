package com.brunobterra.androidchallenge.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.brunobterra.androidchallenge.model.Crianca
import com.brunobterra.androidchallenge.repository.AlunoQuery
import com.brunobterra.androidchallenge.repository.AlunoQueryBuilder
import com.brunobterra.androidchallenge.repository.CriancaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CriancaViewModel(private val repo: CriancaRepository) : ViewModel() {

    private val currentQuery = MutableLiveData<AlunoQueryBuilder>(null)

    val items = currentQuery.switchMap { alunoQueryBuilder: AlunoQueryBuilder ->

        repo.getCriancas(repo.defineQuery(alunoQueryBuilder)).cachedIn(viewModelScope)

    }.asFlow()

    fun changeQuery(builder : AlunoQueryBuilder) {
        currentQuery.value = builder
    }

    fun salvarCrianca(crianca: Crianca, avatar: Drawable) = flow<Exception?> {
        emit(repo.salvarCrianca(crianca, avatar))
    }

}

class CriancaViewModelFactory(val repo: CriancaRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CriancaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CriancaViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}