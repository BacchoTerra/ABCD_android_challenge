package com.brunobterra.androidchallenge.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await

class UsuarioRepository {

    private val mAuth = FirebaseAuth.getInstance()


    suspend fun login(email:String,senha:String) : LoginResponse{

        var novoUsuario : Boolean = false

        val exception : Exception? = try {
            mAuth.signInWithEmailAndPassword(email,senha).await()
            null
        }catch (e:FirebaseAuthInvalidCredentialsException){
            e
        }catch (e: FirebaseAuthInvalidUserException){
            novoUsuario = true
            e
        }catch (e: Exception){
            e
        }

        return LoginResponse(exception,novoUsuario)

    }

    suspend fun registrarUsuario(email:String, senha: String) : Exception?{

        return try {
            mAuth.createUserWithEmailAndPassword(email,senha).await()
            null
        }catch (e: FirebaseAuthWeakPasswordException){
            e
        }catch (e : FirebaseAuthInvalidCredentialsException){
            e
        }catch (e : Exception){
            e
        }

    }

    fun getFirebaseUser()= mAuth.currentUser

}

data class LoginResponse(val exception:Exception?, val novoUsuario : Boolean = false )