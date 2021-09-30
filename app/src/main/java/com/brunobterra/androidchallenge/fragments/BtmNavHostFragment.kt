package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.FragmentBtmNavHostBinding


class BtmNavHostFragment : Fragment() {

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

        val navHostFragment = childFragmentManager.findFragmentById(binder.fragmentMainNavHostFragmentContainer.id) as NavHostFragment
        navController = navHostFragment.navController

        binder.fragmentMainNavHostBtmNavigation.setupWithNavController(navController)

    }
}