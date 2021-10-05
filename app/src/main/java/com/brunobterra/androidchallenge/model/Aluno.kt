package com.brunobterra.androidchallenge.model

/**
 * Classe model base do projeto. Ao instanciar programaticamente, é recomendado definir apenas o atributo `nome`.
 *
 *@author Bruno B. Terra
 */
data class Aluno(
    val nome: String = "Bruno",
    val nomeDePesquisa: String = nome.lowercase(),
    val ano: Int = 1,
    var avatarUrl: String = "",
    var docId: String = ""
)