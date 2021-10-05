package com.brunobterra.androidchallenge.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.brunobterra.androidchallenge.model.Aluno
import com.brunobterra.androidchallenge.repository.AlunoQueryBuilder
import com.brunobterra.androidchallenge.repository.AlunoRepository
import kotlinx.coroutines.flow.flow

/**
 * Classe responsável por todas as interações necessárias entre UI e repositório para abstração de lógica de backend.
 *
 * @param repo um objeto de AlunosRpository que deve ser proveniente da classe Application do projeto.
 * @author Bruno B. Terra
 */
class AlunoViewModel(private val repo: AlunoRepository) : ViewModel() {

    //Observador de uma firestore query.
    private val queryAtual = MutableLiveData<AlunoQueryBuilder>(null)

    //Dados paginados do firestore. São atualizados toda vez que há uma mudança de query.
    val alunos = queryAtual.switchMap { alunoQueryBuilder: AlunoQueryBuilder ->

        repo.getAlunos(repo.definirNovaQuery(alunoQueryBuilder)).cachedIn(viewModelScope)

    }.asFlow()

    /**
     * Define uma nova firestore query.
     *
     * @param builder um objeto de AlunoQueryBuilder feito em UI para uma nova busca de dados.
     */
    fun trocarQuery(builder : AlunoQueryBuilder) {
        queryAtual.value = builder
    }

    /**
     * Cria uma interção com o repositório para salvar o dado aluno ao firestore.
     *
     * @param aluno um objeto de aluno para ser salvo
     * @param avatar um drawable que sera salvo no cloud storage como PNG e tera seu URL salvo no objeto de aluno.
     *
     * @return um flow contento a respota de sucesso ou falha da operação.
     */
    fun salvarAluno(aluno: Aluno, avatar: Drawable) = flow<Exception?> {
        emit(repo.salvarAluno(aluno, avatar))
    }

    /**
     * Cria uma interção com o repositório para trocar o avatar do dado aluno.
     *
     * @param docId id do documento firestore para salvar o novo url.
     * @param newAvatar um novo drawable que substituirá o salvo no cloud storage e no firestore doc.
     *
     * @return um flow contento a respota de sucesso ou falha da operação.
     */
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