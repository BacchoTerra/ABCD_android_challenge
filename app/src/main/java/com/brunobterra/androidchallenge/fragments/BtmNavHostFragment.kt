package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.brunobterra.androidchallenge.databinding.FragmentBtmNavHostBinding
import com.brunobterra.androidchallenge.utils.BtmNavNavigationUtils


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
    private lateinit var btmNavNavHelper : BtmNavNavigationUtils


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

    /**
     * Instancia um navController para realizar navegação pelo bottomNav;
     */
    private fun inicialNavController() {
        val navHostFragment = childFragmentManager.findFragmentById(binder.fragmentMainNavHostFragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController
        btmNavNavHelper = BtmNavNavigationUtils(navController)

    }

    /**
     * Controla toda a lógica de navegação do BottomNavView.
     */
    private fun iniciarBtmNavNavigation() {

        binder.fragmentMainNavHostBtmNavigation.setNavigationChangeListener { view, position ->

            when(position) {

                BTM_NAV_INICIO_POS -> btmNavNavHelper.navegarParaHome()
                BTM_NAV_ALUNO_POS -> btmNavNavHelper.navegarParaAlunos()
                BTM_NAV_AJUDA_POS -> btmNavNavHelper.navegarParaAjuda()
                BTM_NAV_CONFIG_POS ->btmNavNavHelper.navegarParaConfig()

            }
        }
    }

}