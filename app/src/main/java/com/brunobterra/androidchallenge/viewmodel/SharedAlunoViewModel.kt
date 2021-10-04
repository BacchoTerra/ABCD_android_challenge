package com.brunobterra.androidchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brunobterra.androidchallenge.model.Crianca

class SharedAlunoViewModel : ViewModel() {

    val alunoDeEdicao = MutableLiveData<Crianca?>()

    val updateList = MutableLiveData<Boolean>(false)

    fun setAlunoEdicao(crianca: Crianca?){
        alunoDeEdicao.value = crianca
    }

    fun setHasToUpdateList(hasToUpdateList : Boolean){
        updateList.value = hasToUpdateList
    }

}