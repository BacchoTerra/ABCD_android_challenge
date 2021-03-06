package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.adapter.AlunosAdapter
import com.brunobterra.androidchallenge.application.ChallengeApplication
import com.brunobterra.androidchallenge.databinding.FragmentAlunosListaBinding
import com.brunobterra.androidchallenge.model.Aluno
import com.brunobterra.androidchallenge.repository.AlunoQuery
import com.brunobterra.androidchallenge.repository.AlunoQueryBuilder
import com.brunobterra.androidchallenge.utils.AlunosNavigationUtils
import com.brunobterra.androidchallenge.viewmodel.AlunoViewModel
import com.brunobterra.androidchallenge.viewmodel.AlunoViewModelFactory
import com.brunobterra.androidchallenge.viewmodel.SharedAlunoViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlunosListaFragment : Fragment(), View.OnClickListener {

    companion object{

        const val TAB_POS_ORDENAR_POR_NOME = 0
        const val TAB_POS_ORDENAR_POR_ANO = 1

    }

    //ViewModel
    private val alunoViewModel: AlunoViewModel by viewModels {
        AlunoViewModelFactory((requireActivity().application as ChallengeApplication).alunoRepo)
    }
    //Usada para comunicação entre listagem e ediçao de alunos.
    private val sharedAlunoViewModel: SharedAlunoViewModel by activityViewModels()

    //Componentes de layout
    private val binder by lazy {
        FragmentAlunosListaBinding.inflate(layoutInflater)
    }

    //Navigation
    lateinit var navController: NavController
    lateinit var alunosNavigationUtils: AlunosNavigationUtils

    //Firestore query
    private val alunoQueryBuilder = AlunoQueryBuilder()

    //RecycleView
    private lateinit var mAdapter: AlunosAdapter
    private var deveScrollarParaOInicio = false


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

    }

    /**
     * Faz todas as operação necessárias para o funcionamento da UI do fragment.
     */
    private fun init() {
        navController = Navigation.findNavController(binder.root)
        alunosNavigationUtils = AlunosNavigationUtils(navController)

        binder.fragmentAlunosListaFabAdicionar.setOnClickListener(this)
        binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosImageFechar.setOnClickListener(
            this
        )

        observarSeDeveAtualizarDados()

    }

    /**
     * Instancia o adapter de display de alunos e vincula a recyclerView com todos os objetos necessarios.
     * Também é responsável por chamar todos os métodos relacionados com a recyclerView.
     */
    private fun initRecyclerView() {

        mAdapter = AlunosAdapter(requireActivity()) {
            iniciarFragmentDeEdicaoDeAluno(it)
        }

        with(binder.fragmentAlunosListaRecyclerAlunos) {

            adapter = mAdapter
            layoutManager =
                GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        coletarAlunos()
        adicionarLoadStateListener()
        iniciarOrdenarPorNomeOuAno()
        iniciarFiltragemPorNome()

    }

    /**
     * Compartilha um objeto de aluno com uma sharedViewModel e navega para o fragment de edição de ALuno.
     *
     * @param aluno o objeto de aluno alvo da edição.
     */
    private fun iniciarFragmentDeEdicaoDeAluno(aluno: Aluno) {
        sharedAlunoViewModel.setAlunoEdicao(aluno)
        alunosNavigationUtils.navegarParaCriadAlunosFragment()
    }

    /**
     * Lança uma coroutine para início do processo de coleta de dados paginados de Alunos do firestore
     */
    private fun coletarAlunos() {
        alunoViewModel.trocarQuery(alunoQueryBuilder)

        lifecycleScope.launch {

            alunoViewModel.alunos.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    /**
     * Adiciona informações de Loading da busca de dados de Alunos para notificar o usuário do estado atual da busca.
     */
    private fun adicionarLoadStateListener() {

        mAdapter.addLoadStateListener { loadState ->


            val carregando = loadState.source.refresh is LoadState.Loading
            binder.fragmentAlunosListaProgress.isVisible = carregando


            if (!carregando && loadState.source.refresh !is LoadState.Error && deveScrollarParaOInicio) {
                binder.fragmentAlunosListaRecyclerAlunos.layoutManager?.scrollToPosition(0)
                deveScrollarParaOInicio = false
            }

            //AlunosPagingSource retorna um ArrayOutOfBoundsException quando a pesquisa retorna 0 resultados.
            if (!carregando && loadState.source.refresh is LoadState.Error) {
                if ((loadState.source.refresh as LoadState.Error).error is ArrayIndexOutOfBoundsException){
                    binder.fragmentAlunosListaTxtSemResultados.isVisible = true
                    binder.fragmentAlunosListaRecyclerAlunos.isVisible = false
                }
            }else{
                binder.fragmentAlunosListaTxtSemResultados.isVisible = false
                binder.fragmentAlunosListaRecyclerAlunos.isVisible = true
            }

        }
    }

    /**
     * Observa constantemente uma SharedViewModel para detectar se há alguma alteração local que requer atualização dos dados obtidos no firestore.
     */
    private fun observarSeDeveAtualizarDados() {

        sharedAlunoViewModel.deveAtualizarDados.observe(viewLifecycleOwner) { deveAtualizar ->

            if (deveAtualizar) {
                mAdapter.refresh()
                deveScrollarParaOInicio = true
                sharedAlunoViewModel.setDeveAtualizarDados(false)
            }
        }

    }

    /**
     * Contém toda a lógica para criar Firestore queries que ordenam os dados por nome ou ano.
     */
    private fun iniciarOrdenarPorNomeOuAno() {

        binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosTabLayoutFiltrarPor.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {

                    if (binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosTabLayoutFiltrarPor.getTabAt(
                            TAB_POS_ORDENAR_POR_NOME
                        )?.isSelected == true
                    ) {
                        habilitarCaixaDeTextoDePesquisa(true)
                        alunoQueryBuilder.orderBy = AlunoQuery.ORDER_NAME
                    } else if (binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosTabLayoutFiltrarPor.getTabAt(
                            TAB_POS_ORDENAR_POR_ANO
                        )?.isSelected == true
                    ) {
                        habilitarCaixaDeTextoDePesquisa(false)
                        alunoQueryBuilder.orderBy = AlunoQuery.ORDER_ANO
                    }

                    alunoViewModel.trocarQuery(alunoQueryBuilder)
                    deveScrollarParaOInicio = true
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })

    }

    /**
     * A caixa de texto de pesquisa é habilitada por padrão. Porém esete método vai desabilita-la quando o usuário decidir
     * ordenar os dados por ano ao invés de nome.
     *
     * @param habilitar boolean que controla se a caixa de texto está habilitada para ediçao.
     */
    private fun habilitarCaixaDeTextoDePesquisa(habilitar: Boolean) {

        if (!habilitar) binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosEditPesquisa.clearFocus()

        binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosEditPesquisa.isEnabled = habilitar

    }

    /**
     * Contém toda a lógica para criar Firestore queries que ordenam os dados por nome ou ano.
     */
    private fun iniciarFiltragemPorNome() {

        binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosEditPesquisa.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    //Significa a caixa de texto foi editada pelo proprio usuario.
                    if (p2 != p3) {

                        val nome = p0.toString()

                        if (nome.length >= 3) {
                            setQueryParaFiltrarPorNome(nome)

                            //Significa que o usuario esta deletando o texto e, quando chegar em 2 chars, nao vai mais ter query por nome.
                        } else if (nome.length == 2 && p2 > p3) {
                            setQueryParaFiltrarPorNome(null)

                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

    }

    /**
     * Se o usuário alterar os dados da caixa de texto de pesquisa de forma que permita uma query, este método sera chamado para gerar uma nova firestore
     * query com os novos dados de filtro.
     *
     * @see iniciarFiltragemPorNome
     */
    private fun setQueryParaFiltrarPorNome(name: String?) {
        alunoQueryBuilder.nameQuery = name?.lowercase()
        alunoViewModel.trocarQuery(alunoQueryBuilder)
        deveScrollarParaOInicio = true
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            binder.fragmentAlunosListaFabAdicionar.id -> {
                alunosNavigationUtils.navegarParaCriadAlunosFragment()
            }

            binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosImageFechar.id -> {
                if (binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosEditPesquisa.text.toString()
                        .isNotEmpty()
                ) {
                    binder.fragmentAlunosListaContentPesquisar.contentPesquisarAlunosEditPesquisa.text =
                        null
                    setQueryParaFiltrarPorNome(null)
                }
            }

        }

    }

}