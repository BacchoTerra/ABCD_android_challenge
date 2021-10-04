package com.brunobterra.androidchallenge.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val mAuth = FirebaseAuth.getInstance()


    /**
     * Este método é responsalvel por tentar fazer o login do usuario.
     * No caso de gerar uma excessão do tipo `FirebaseAuthInvalidUserException`, significando que o email não foi previamente cadastrado,
     * ele informa o tipo de retorno que é um novo usuario.
     *
     * @param email E-mail do usuário.
     * @param senha senha do usuário.
     *
     * @return LoginResponse contendo uma exception (caso a operação de login tenha falhado),
     * e um Boolen informando se é um usuario não registrado previamente.
     */
    suspend fun login(email: String, senha: String): LoginResponse {

        var novoUsuario: Boolean = false

        val exception: Exception? = try {
            mAuth.signInWithEmailAndPassword(email, senha).await()
            null
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e
        } catch (e: FirebaseAuthInvalidUserException) {
            novoUsuario = true
            e
        } catch (e: Exception) {
            e
        }

        return LoginResponse(exception, novoUsuario)

    }

    /**
     * Esse método realiza a operação de registrar o usuario no FirebaseAuth.
     * Ele só vai ser chamado se o método `login` retornar uma exception do tipo `FirebaseAuthInvalidUserException`,
     * consequentemente retornando um `true` em `LoginResponse`.
     *
     * @param email E-mail do usuário.
     * @param senha senha do usuário.
     *
     *@see login
     *@return Uma exception caso o registro falhe.
     */
    suspend fun registrarUsuario(email: String, senha: String): Exception? {

        return try {
            mAuth.createUserWithEmailAndPassword(email, senha).await()
            null
        } catch (e: FirebaseAuthWeakPasswordException) {
            e
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e
        } catch (e: Exception) {
            e
        }

    }

    suspend fun sendPasswordResetEmail(email:String) : Exception?{
        return try {
            mAuth.sendPasswordResetEmail(email).await()
            null
        }catch (e:Exception){
            e
        }
    }

    /**
     * Responsavel por verificar se ja existe um usuario logado.
     *
     * @return FirebaseUser null caso não tenha um usuario logado, e não null caso ja tenha.
     */
    fun getFirebaseUser() = mAuth.currentUser

}

/**
 * Classe model responsavel pelo retorno de informações de operação de login.
 */
data class LoginResponse(val exception: Exception?, val novoUsuario: Boolean = false)