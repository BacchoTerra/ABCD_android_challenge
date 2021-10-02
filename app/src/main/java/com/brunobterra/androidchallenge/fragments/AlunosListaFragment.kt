package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.adapter.CriancasAdapter
import com.brunobterra.androidchallenge.databinding.FragmentAlunosListaBinding


class AlunosListaFragment : Fragment(),View.OnClickListener {

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
        init()
        initRecyclerView()
    }

    private fun init() {
        navController = Navigation.findNavController(binder.root)

        binder.fragmentAlunosListaFabAdicionar.setOnClickListener(this)

    }

    private fun initRecyclerView() {

        val mAdapter = CriancasAdapter(requireActivity())

        with(binder.fragmentAlunosListaRecyclerCriacas){

            adapter = mAdapter
            layoutManager = GridLayoutManager(requireActivity(),2,GridLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
        }
    }

    override fun onClick(p0: View?) {

        when(p0?.id) {

            binder.fragmentAlunosListaFabAdicionar.id -> {
                navController.navigate(R.id.action_alunosListaFragment_to_alunosAdicionarFragment)
            }

        }

    }


}