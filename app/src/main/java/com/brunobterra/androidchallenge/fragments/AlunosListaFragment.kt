package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.adapter.CriancasAdapter
import com.brunobterra.androidchallenge.application.ChallengeApplication
import com.brunobterra.androidchallenge.databinding.FragmentAlunosListaBinding
import com.brunobterra.androidchallenge.repository.AlunoQuery
import com.brunobterra.androidchallenge.repository.AlunoQueryBuilder
import com.brunobterra.androidchallenge.viewmodel.CriancaViewModel
import com.brunobterra.androidchallenge.viewmodel.CriancaViewModelFactory
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlunosListaFragment : Fragment(), View.OnClickListener {

    //ViewModel
    private val criancaViewModel: CriancaViewModel by viewModels {
        CriancaViewModelFactory((requireActivity().application as ChallengeApplication).criancaRepo)
    }

    //Componentes de layout
    private val binder by lazy {
        FragmentAlunosListaBinding.inflate(layoutInflater)
    }

    //Navigation
    lateinit var navController: NavController

    //Firestore query
    private val alunoQueryBuilder = AlunoQueryBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initFilterByNameOrYear()
        initQueryByName()

    }

    private fun init() {
        navController = Navigation.findNavController(binder.root)

        binder.fragmentAlunosListaFabAdicionar.setOnClickListener(this)

    }

    private fun initRecyclerView() {

        val mAdapter = CriancasAdapter(requireActivity())

        criancaViewModel.changeQuery(alunoQueryBuilder)

        lifecycleScope.launch {

            criancaViewModel.items.collectLatest {
                mAdapter.submitData(it)
            }
        }

        with(binder.fragmentAlunosListaRecyclerCriancas) {

            adapter = mAdapter
            layoutManager =
                GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
    }

    private fun initFilterByNameOrYear() {

        binder.fragmentAlunosListaContentSearch.contentAlunosSearchTabLayoutFiltrarPor.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {

                    if (binder.fragmentAlunosListaContentSearch.contentAlunosSearchTabLayoutFiltrarPor.getTabAt(
                            0
                        )?.isSelected == true
                    ) {
                        enableEditName(true)
                        alunoQueryBuilder.orderBy = AlunoQuery.ORDER_NAME
                    } else if (binder.fragmentAlunosListaContentSearch.contentAlunosSearchTabLayoutFiltrarPor.getTabAt(
                            1
                        )?.isSelected == true
                    ) {
                        enableEditName(false)
                        alunoQueryBuilder.orderBy = AlunoQuery.ORDER_ANO
                    }

                    criancaViewModel.changeQuery(alunoQueryBuilder)
                    binder.fragmentAlunosListaRecyclerCriancas.layoutManager?.scrollToPosition(0)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })

    }

    private fun enableEditName(enabled: Boolean) {

        if (!enabled) binder.fragmentAlunosListaContentSearch.contentAlunosSearchEditPesquisa.clearFocus()

        binder.fragmentAlunosListaContentSearch.contentAlunosSearchEditPesquisa.isEnabled = enabled

    }

    private fun initQueryByName() {

        binder.fragmentAlunosListaContentSearch.contentAlunosSearchEditPesquisa.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {

                    val nome = p0.toString()

                    if (nome.length >= 3) {
                        alunoQueryBuilder.nameQuery = nome.lowercase()
                    } else {
                        alunoQueryBuilder.nameQuery = null
                    }
                    criancaViewModel.changeQuery(alunoQueryBuilder)

                }

            })

    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            binder.fragmentAlunosListaFabAdicionar.id -> {
                navController.navigate(R.id.action_alunosListaFragment_to_alunosAdicionarFragment)
            }

        }

    }

}