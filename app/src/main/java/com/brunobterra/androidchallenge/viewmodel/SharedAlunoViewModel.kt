package com.brunobterra.androidchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brunobterra.androidchallenge.model.Crianca

class SharedAlunoViewModel : ViewModel() {

    val alunoDeEdicao = MutableLiveData<Crianca?>()

    fun setAlunoEdicao(crianca: Crianca?){
        alunoDeEdicao.value = crianca
    }

}