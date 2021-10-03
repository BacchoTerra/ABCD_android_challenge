package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.adapter.CriancasAdapter
import com.brunobterra.androidchallenge.application.ChallengeApplication
import com.brunobterra.androidchallenge.databinding.FragmentAlunosListaBinding
import com.brunobterra.androidchallenge.model.Crianca
import com.brunobterra.androidchallenge.utils.shortToast
import com.brunobterra.androidchallenge.viewmodel.CriancaViewModel
import com.brunobterra.androidchallenge.viewmodel.CriancaViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlunosListaFragment : Fragment(),View.OnClickListener {

    //ViewModel
    private val criancaViewModel: CriancaViewModel by navGraphViewModels(R.id.alunos_nav_graph) {
        CriancaViewModelFactory((requireActivity().application as ChallengeApplication).criancaRepo)
    }

    //Componentes de layout
    private val binder by lazy {
        FragmentAlunosListaBinding.inflate(layoutInflater)
    }

    //Navigation
    lateinit var navController: NavController
    val ultimaCriancaSalva : Crianca? = null


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
        observeCriancaSalva()
    }

    private fun init() {
        navController = Navigation.findNavController(binder.root)

        binder.fragmentAlunosListaFabAdicionar.setOnClickListener(this)

    }

    private fun initRecyclerView() {

        val mAdapter = CriancasAdapter(requireActivity())

        lifecycleScope.launch {
            criancaViewModel.criancas.collectLatest {
                mAdapter.submitData(it)
            }
        }

        with(binder.fragmentAlunosListaRecyclerCriancas){

            adapter = mAdapter
            layoutManager = GridLayoutManager(requireActivity(),2,GridLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
        }
    }

    private fun observeCriancaSalva() {
        criancaViewModel.ultimaCriancaSalva.observe(viewLifecycleOwner){
            it?.let {
                shortToast("mudou")
                //todo: Esse metodo, nessa parte aqui vai se usada para update data quando adicionar uma nova crianca.
            }
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