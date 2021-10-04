package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.application.ChallengeApplication
import com.brunobterra.androidchallenge.databinding.FragmentAlunosAdicionarBinding
import com.brunobterra.androidchallenge.model.Crianca
import com.brunobterra.androidchallenge.utils.shortToast
import com.brunobterra.androidchallenge.viewmodel.CriancaViewModel
import com.brunobterra.androidchallenge.viewmodel.CriancaViewModelFactory
import com.brunobterra.androidchallenge.viewmodel.SharedAlunoViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlunosAdicionarFragment : Fragment(), View.OnClickListener {

    //layout components
    private val binder by lazy {
        FragmentAlunosAdicionarBinding.inflate(layoutInflater)
    }

    //ViewModel
    private val criancaViewModel: CriancaViewModel by viewModels {
        CriancaViewModelFactory((requireActivity().application as ChallengeApplication).criancaRepo)
    }
    private val sharedAlunoViewModel: SharedAlunoViewModel by activityViewModels()

    //Lista de avatares
    val avataresList = listOf<Int>(
        R.drawable.avatar_juju,
        R.drawable.avatar_magi,
        R.drawable.avatar_nina,
        R.drawable.avatar_ozi,
        R.drawable.avatar_sr_goiaba,
        R.drawable.avatar_zig
    )
    var posAvatarAtual = 0
    var avatarAtual = avataresList[posAvatarAtual]

    //Objeto de aluno para edição
    private var alunoEdicao: Crianca? = null
    private var hasEditingAlunoChangedAvatar = false

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

    private fun init() {

        binder.fragmentAlunosAdicionarContentCustomToolbar.contentAdicionarAlunoCustomToolbarImageBack.setOnClickListener(
            this
        )
        binder.fragmentAlunosAdicionarBtnMudarAvatar.setOnClickListener(this)
        binder.fragmentAlunosAdicionarBtnSalvar.setOnClickListener(this)

        Glide.with(requireActivity()).load(avatarAtual)
            .into(binder.fragmentAlunosAdicionarImageAvatar)

        retrieveAlunoIfEditing()

    }

    private fun retrieveAlunoIfEditing() {

        sharedAlunoViewModel.alunoDeEdicao.observe(viewLifecycleOwner) {
            alunoEdicao = it
            bindLayoutWithEditindAluno()
        }
    }

    private fun bindLayoutWithEditindAluno() {

        alunoEdicao?.let {
            binder.fragmentAlunosAdicionarEditNome.setText(it.nome)
            Glide.with(requireActivity()).load(it.avatarUrl)
                .into(binder.fragmentAlunosAdicionarImageAvatar)
            binder.fragmentAlunosAdicionarCheckBoxAutorizacao.isChecked = true
            binder.fragmentAlunosAdicionarEditNome.isEnabled = false

        }
    }

    private fun mudarAvatar() {

        posAvatarAtual = if (posAvatarAtual == avataresList.lastIndex) 0 else ++posAvatarAtual
        avatarAtual = avataresList[posAvatarAtual]
        Glide.with(requireActivity()).load(avatarAtual)
            .into(binder.fragmentAlunosAdicionarImageAvatar)

        alunoEdicao?.let {
            hasEditingAlunoChangedAvatar = true
        }

    }

    private suspend fun salvarCrianca() {

        val nome = binder.fragmentAlunosAdicionarEditNome.text.toString()

        val avatarDrawable = ContextCompat.getDrawable(requireActivity(), avatarAtual)!!
        val crianca = Crianca(nome)

        setLayoutCarregando(true)

        criancaViewModel.salvarCrianca(crianca, avatarDrawable)
            .collectLatest { exception: Exception? ->

                exception?.let {

                    shortToast(R.string.toast_algo_deu_errado)
                    setLayoutCarregando(false)
                    return@collectLatest

                }

                sharedAlunoViewModel.setHasToUpdateList(true)
                navegarUp()

            }
    }

    private suspend fun updateCrianca() {

        val newAvatar = ContextCompat.getDrawable(requireContext(), avatarAtual)!!

        setLayoutCarregando(true)
        criancaViewModel.updateCrianca(alunoEdicao!!.docId, newAvatar).collectLatest { exception ->

            exception?.let {
                setLayoutCarregando(false)
                shortToast(R.string.toast_algo_deu_errado)
            }
            sharedAlunoViewModel.setHasToUpdateList(true)
            navegarUp()
        }

    }

    private fun setLayoutCarregando(carregando: Boolean) {

        binder.fragmentAlunosAdicionarProgressSalvando.isVisible = carregando
        binder.fragmentAlunosAdicionarBtnSalvar.isVisible = !carregando
        binder.fragmentAlunosAdicionarContentCustomToolbar.contentAdicionarAlunoCustomToolbarImageBack.isEnabled =
            !carregando

    }

    private fun podeSalvarCrianca(): Boolean {

        return if (binder.fragmentAlunosAdicionarEditNome.text.toString().isBlank()) {
            shortToast(R.string.toast_salvar_crianca_preencha_nome)
            false
        } else if (!binder.fragmentAlunosAdicionarCheckBoxAutorizacao.isChecked) {
            shortToast(R.string.toast_salvar_crianca_politica_privacidade)
            false
        } else if(alunoEdicao != null && !hasEditingAlunoChangedAvatar){
            shortToast(R.string.toast_update_nao_trocou_avatar)
            false
        }else {
            true
        }

    }

    private fun navegarUp() {
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedAlunoViewModel.setAlunoEdicao(null)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            binder.fragmentAlunosAdicionarContentCustomToolbar.contentAdicionarAlunoCustomToolbarImageBack.id -> navegarUp()
            binder.fragmentAlunosAdicionarBtnMudarAvatar.id -> mudarAvatar()
            binder.fragmentAlunosAdicionarBtnSalvar.id -> {

                lifecycleScope.launch {

                    if (!podeSalvarCrianca()) return@launch

                    alunoEdicao?.let {
                        updateCrianca()
                        return@launch
                    }
                    salvarCrianca()
                }

            }
        }
    }


}