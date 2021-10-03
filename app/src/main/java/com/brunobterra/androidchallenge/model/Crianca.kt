package com.brunobterra.androidchallenge.model

data class Crianca(
    val nome: String = "Bruno",
    val nomeDePesquisa: String = nome.lowercase(),
    val ano: Int = 1,
    var avatarUrl: String = "",
    var docId: String = ""
)