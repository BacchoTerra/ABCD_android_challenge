package com.brunobterra.androidchallenge.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.brunobterra.androidchallenge.model.Aluno
import com.brunobterra.androidchallenge.repository.AlunoQueryBuilder
import com.brunobterra.androidchallenge.repository.AlunoRepository
import kotlinx.coroutines.flow.flow

class AlunoViewModel(private val repo: AlunoRepository) : ViewModel() {

    private val queryAtual = MutableLiveData<AlunoQueryBuilder>(null)

    val alunos = queryAtual.switchMap { alunoQueryBuilder: AlunoQueryBuilder ->

        repo.getAlunos(repo.definirNovaQuery(alunoQueryBuilder)).cachedIn(viewModelScope)

    }.asFlow()

    fun trocarQuery(builder : AlunoQueryBuilder) {
        queryAtual.value = builder
    }

    fun salvarAluno(aluno: Aluno, avatar: Drawable) = flow<Exception?> {
        emit(repo.salvarAluno(aluno, avatar))
    }

    fun updateAluno(docId : String, newAvatar:Drawable) = flow <Exception?>{
        emit(repo.updateAluno(docId,newAvatar))
    }

}

class AlunoViewModelFactory(val repo: AlunoRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlunoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlunoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}