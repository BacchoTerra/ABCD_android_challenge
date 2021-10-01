package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.FragmentBtmNavHostBinding


class BtmNavHostFragment : Fragment() {

    companion object{

        const val BTM_NAV_INICIO_POS = 0
        const val BTM_NAV_ALUNO_POS = 1
        const val BTM_NAV_AJUDA_POS = 2
        const val BTM_NAV_CONFIG_POS = 3

    }

    //Componentes de layout
    private val binder by lazy {
        FragmentBtmNavHostBinding.inflate(layoutInflater)
    }

    //NavigationController
    private lateinit var navController : NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicialNavController()
        iniciarBtmNavNavigation()

    }

    private fun inicialNavController() {
        val navHostFragment = childFragmentManager.findFragmentById(binder.fragmentMainNavHostFragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun iniciarBtmNavNavigation() {

        binder.fragmentMainNavHostBtmNavigation.setNavigationChangeListener { view, position ->

            when(position) {

                BTM_NAV_INICIO_POS -> navegarParaItem(R.id.menu_main_nav_host_principal)
                BTM_NAV_ALUNO_POS -> navegarParaItem(R.id.menu_main_nav_host_aluno)
                BTM_NAV_AJUDA_POS -> navegarParaItem(R.id.menu_main_nav_host_informacoes)
                BTM_NAV_CONFIG_POS -> navegarParaItem(R.id.menu_main_nav_host_configuracoes)

            }
        }
    }

    private fun navegarParaItem(@IdRes resId:Int ){
        navController.popBackStack()
        navController.navigate(resId)
    }

}