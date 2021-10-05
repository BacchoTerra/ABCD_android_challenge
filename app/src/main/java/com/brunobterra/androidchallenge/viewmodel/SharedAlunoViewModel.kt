package com.brunobterra.androidchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brunobterra.androidchallenge.model.Aluno

class SharedAlunoViewModel : ViewModel() {

    val alunoEmEdicao = MutableLiveData<Aluno?>()

    val deveAtualizarDados = MutableLiveData<Boolean>(false)

    fun setAlunoEdicao(aluno: Aluno?){
        alunoEmEdicao.value = aluno
    }

    fun setDeveAtualizarDados(deveAtualizar : Boolean){
        deveAtualizarDados.value = deveAtualizar
    }

}