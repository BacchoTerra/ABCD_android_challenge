package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.FragmentAlunosAdicionarBinding


class AlunosAdicionarFragment : Fragment() {

    //layout components
    private val binder by lazy {
        FragmentAlunosAdicionarBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        return binder.root
    }


}