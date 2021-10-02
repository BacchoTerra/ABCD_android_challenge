package com.brunobterra.androidchallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.brunobterra.androidchallenge.repository.LoginResponse
import com.brunobterra.androidchallenge.repository.UsuarioRepository
import kotlinx.coroutines.flow.flow

class UsuarioViewModel(val repo: UsuarioRepository) : ViewModel() {

    val mFirebaseUser = repo.getFirebaseUser()

    fun login(email:String,senha:String) = flow<LoginResponse> {
        emit(repo.login(email,senha))
    }

    fun registrarUsuario(email:String,senha:String) = flow<Exception?> {
        emit(repo.registrarUsuario(email,senha))
    }

}

class UsuarioViewModelFactory(val repo: UsuarioRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}