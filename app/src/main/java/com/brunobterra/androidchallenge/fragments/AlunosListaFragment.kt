package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.FragmentAlunosListaBinding


class AlunosListaFragment : Fragment() {

    lateinit var navController: NavController

    private val binder by lazy {
        FragmentAlunosListaBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binder.root)

        binder.fragmentAlunosListaFabAdicionar.setOnClickListener {


            navController.navigate(R.id.action_alunosListaFragment_to_alunosAdicionarFragment)

        }
    }


}