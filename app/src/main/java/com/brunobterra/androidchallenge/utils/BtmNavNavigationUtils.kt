package com.brunobterra.androidchallenge.utils

import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.brunobterra.androidchallenge.R

/**
 * Helper class para abstrair navegação do código UI.
 *
 * @author Bruno B. Terra
 */
class BtmNavNavigationUtils (private val navController : NavController){

    /**
     * Navega para o fragment home
     */
    fun navegarParaHome(){
        navegarPara(R.id.menu_main_nav_host_principal)
    }

    /**
     * Navega para o fragment alunos
     */
    fun navegarParaAlunos(){
        navegarPara(R.id.menu_main_nav_host_aluno)
    }

    /**
     * Navega para o fragment ajuda
     */
    fun navegarParaAjuda(){
        navegarPara(R.id.menu_main_nav_host_informacoes)
    }

    /**
     * Navega para o fragment config
     */
    fun navegarParaConfig(){
        navegarPara(R.id.menu_main_nav_host_configuracoes)
    }

    /**
     * Navega diretamente para o fragment com o dado parâmetro.
     *
     * @param resId id da action da navegação.
     */
    private fun navegarPara(@IdRes resId:Int ){
        navController.popBackStack()
        navController.navigate(resId)
    }

}