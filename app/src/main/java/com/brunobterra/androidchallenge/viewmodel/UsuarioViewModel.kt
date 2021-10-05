package com.brunobterra.androidchallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.brunobterra.androidchallenge.repository.LoginResponse
import com.brunobterra.androidchallenge.repository.UsuarioRepository
import kotlinx.coroutines.flow.flow

/**
 * Classe responsável por todas as interações necessárias entre UI e repositório para abstração de lógica de backend.
 *
 * @param repo um objeto de UsuarioRepository que deve ser proveniente da classe Application do projeto.
 *
 * @author Bruno B. Terra
 */
class UsuarioViewModel(val repo: UsuarioRepository) : ViewModel() {

    val mFirebaseUser = repo.getFirebaseUser()

    /**
     * Realiza o login do usuário no FirebaseAuth.
     *
     * @param email email do usuário
     * @param senha senha do usuário
     *
     *@return um flow contendo a resposta de sucesso da operação.
     */
    fun login(email: String, senha: String) = flow<LoginResponse> {
        emit(repo.login(email, senha))
    }

    /**
     * Realiza o regiistro do usuário no FirebaseAuth.
     *
     * @param email email do usuário
     * @param senha senha do usuário
     *
     *@return um flow contendo a resposta de sucesso da operação.
     */
    fun registrarUsuario(email: String, senha: String) = flow<Exception?> {
        emit(repo.registrarUsuario(email, senha))
    }

    /**
     * Chama um pedido de envio de email para reset de senha do usuário registrado previamente no FirebaseAuth.
     *
     * @param email email de destino.
     *
     *@return um flow contendo a resposta de sucesso da operação.
     */
    fun enviarEmailDeResetDeSenha(email: String) =
        flow<Exception?> { emit(repo.enviarEmailDeResetDeSenha(email)) }

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