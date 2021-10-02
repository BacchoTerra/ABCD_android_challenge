package com.brunobterra.androidchallenge.application

import android.app.Application
import com.brunobterra.androidchallenge.repository.UsuarioRepository
import com.google.firebase.FirebaseApp

class ChallengeApplication : Application() {

    lateinit var usuarioRepo : UsuarioRepository



    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        usuarioRepo = UsuarioRepository()

    }

}