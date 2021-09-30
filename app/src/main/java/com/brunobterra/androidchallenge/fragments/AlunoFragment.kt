package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.FragmentAlunoBinding


class AlunoFragment : Fragment() {

    //Componentes de layout
    private val binder by lazy {
        FragmentAlunoBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binder.root
    }

}