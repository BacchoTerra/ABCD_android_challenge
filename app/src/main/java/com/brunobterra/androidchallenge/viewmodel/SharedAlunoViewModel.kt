package com.brunobterra.androidchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.brunobterra.androidchallenge.model.Aluno

/**
 * Classe responsável por permitir troca de objetos pesados entre fragments que compartilham estes.
 *
 * @author Bruno B. Terra
 */
class SharedAlunoViewModel : ViewModel() {

    //Quando se está editando um objeto de aluno, esse observador deve ser usado para recebe-lo.
    val alunoEmEdicao = MutableLiveData<Aluno?>()

    //Define quando os dados atuais do firestore devem ser atualizados
    val deveAtualizarDados = MutableLiveData<Boolean>(false)

    /**
     * Atualiza o aluno que está sendo editado.
     *
     * @param aluno objeto de aluno para editar.
     */
    fun setAlunoEdicao(aluno: Aluno?){
        alunoEmEdicao.value = aluno
    }


    /**
     * Atualiza o observador se os dados do firestore atuais devem ser atualizados.
     *
     * @param deveAtualizar controla se deve ou não atualizar para novos dados.
     */
    fun setDeveAtualizarDados(deveAtualizar : Boolean){
        deveAtualizarDados.value = deveAtualizar
    }

}