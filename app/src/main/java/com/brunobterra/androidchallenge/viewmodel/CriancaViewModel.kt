package com.brunobterra.androidchallenge.viewmodel

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.brunobterra.androidchallenge.model.Crianca
import com.brunobterra.androidchallenge.repository.CriancaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CriancaViewModel(private val repo: CriancaRepository) : ViewModel() {

    val ultimaCriancaSalva = MutableLiveData<Crianca?>(null)

    private val pagedAdata : Flow<PagingData<Crianca>>? = null

    val criancas = pagedAdata?:repo.getCriancas().cachedIn(viewModelScope)

    fun setUltimaCriancaSalva(crianca:Crianca?){
        ultimaCriancaSalva.value = crianca
    }

    fun salvarCrianca(crianca: Crianca, avatar:Drawable) = flow<Exception?> {
        emit(repo.salvarCrianca(crianca,avatar))
    }

}

class CriancaViewModelFactory(val repo : CriancaRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CriancaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CriancaViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}