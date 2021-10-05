package com.brunobterra.androidchallenge.utils

import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.brunobterra.androidchallenge.R

/**
 * Helper class para abstrair navegação do código UI.
 *
 * @author Bruno B. Terra
 */
class AlunosNavigationUtils (private val navController : NavController){

    /**
     * Navega para o fragment AlunosAdicionarFragment
     */
    fun navegarParaCriadAlunosFragment(){
        navegarPara(R.id.action_alunosListaFragment_to_alunosAdicionarFragment)
    }

    /**
     * Navega diretamente para o fragment com o dado parâmetro.
     *
     * @param resId id da action da navegação.
     */
    private fun navegarPara(@IdRes resId:Int ){
        navController.navigate(resId)
    }

}