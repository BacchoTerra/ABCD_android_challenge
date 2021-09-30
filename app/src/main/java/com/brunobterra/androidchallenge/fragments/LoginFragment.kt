package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    //Componentes de layout
    private val binder by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binder.root)

        binder.button.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_mainNavHostFragment)
        }

    }


}