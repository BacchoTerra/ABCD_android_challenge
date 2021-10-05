package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.application.ChallengeApplication
import com.brunobterra.androidchallenge.databinding.FragmentAlunosAdicionarBinding
import com.brunobterra.androidchallenge.model.Aluno
import com.brunobterra.androidchallenge.utils.shortToast
import com.brunobterra.androidchallenge.viewmodel.AlunoViewModel
import com.brunobterra.androidchallenge.viewmodel.AlunoViewModelFactory
import com.brunobterra.androidchallenge.viewmodel.SharedAlunoViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AlunosAdicionarFragment : Fragment(), View.OnClickListener {

    //Componentes de layout
    private val binder by lazy {
        FragmentAlunosAdicionarBinding.inflate(layoutInflater)
    }

    //ViewModel
    private val alunoViewModel: AlunoViewModel by viewModels {
        AlunoViewModelFactory((requireActivity().application as ChallengeApplication).alunoRepo)
    }
    //Usada para comunicação entre listagem e ediçao de alunos.
    private val sharedAlunoViewModel: SharedAlunoViewModel by activityViewModels()

    //Lista de avatares
    val listaDeAvatares = listOf<Int>(
        R.drawable.avatar_juju,
        R.drawable.avatar_magi,
        R.drawable.avatar_nina,
        R.drawable.avatar_ozi,
        R.drawable.avatar_sr_goiaba,
        R.drawable.avatar_zig
    )
    var posAvatarAtual = 0
    var avatarAtual = listaDeAvatares[posAvatarAtual]

    //Objeto de aluno para edição
    private var alunoEmEdicao: Aluno? = null
    private var trocouOAvatarDoAlunoEmEdicao = false

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

        binder.fragmentAlunosAdicionarContentCustomToolbar.contentAdicionarAlunoCustomToolbarImageVoltar.setOnClickListener(this)
        binder.fragmentAlunosAdicionarBtnMudarAvatar.setOnClickListener(this)
        binder.fragmentAlunosAdicionarBtnSalvar.setOnClickListener(this)

        Glide.with(requireActivity()).load(avatarAtual)
            .into(binder.fragmentAlunosAdicionarImageAvatar)

        recuperarAlunoEmEdicao()

    }

    /**
     * Caso esse fragment seja proveniente de uma edição de Aluno ao invés de criação, esse método
     * acessa uma sharedViewModel para buscar o objeto de Aluno alvo da edição
     *
     */
    private fun recuperarAlunoEmEdicao() {

        sharedAlunoViewModel.alunoEmEdicao.observe(viewLifecycleOwner) {
            alunoEmEdicao = it
            vincularLayoutComAlunoEmEdicao()
        }
    }

    /**
     * No caso de edição, esse método vincular os dados do objeto de Aluno com o UI.Permitindo a edição do avatar do mesmo.
     * Caso não esteja editando, este método não apresenta função nenhuma.
     *
     * @see mudarAvatar
     */
    private fun vincularLayoutComAlunoEmEdicao() {

        alunoEmEdicao?.let {
            binder.fragmentAlunosAdicionarEditNome.setText(it.nome)
            Glide.with(requireActivity()).load(it.avatarUrl)
                .into(binder.fragmentAlunosAdicionarImageAvatar)
            binder.fragmentAlunosAdicionarCheckBoxAutorizacao.isChecked = true
            binder.fragmentAlunosAdicionarEditNome.isEnabled = false

        }
    }

    /**
     * Identifica qual avatar está atualmente como atual, e muda a posição da lista de avatares para definir um novo avatar atual para o Aluno.
     */
    private fun mudarAvatar() {

        posAvatarAtual = if (posAvatarAtual == listaDeAvatares.lastIndex) 0 else ++posAvatarAtual
        avatarAtual = listaDeAvatares[posAvatarAtual]
        Glide.with(requireActivity()).load(avatarAtual)
            .into(binder.fragmentAlunosAdicionarImageAvatar)

        alunoEmEdicao?.let {
            trocouOAvatarDoAlunoEmEdicao = true
        }

    }

    /**
     * Após a inserção dos dados obrigatórios, este método vai criar o processo de salvamento do objeto de Aluno no Firestore.
     * Caso tenha sucesso, o fragment será fechado, caso não, o fragment fornecerá a opçao de tentar novamente.
     *
     * @see setLayoutCarregando
     */
    private suspend fun salvarAluno() {

        val nome = binder.fragmentAlunosAdicionarEditNome.text.toString()

        val avatarDrawable = ContextCompat.getDrawable(requireActivity(), avatarAtual)!!
        val aluno = Aluno(nome)

        setLayoutCarregando(true)

        alunoViewModel.salvarAluno(aluno, avatarDrawable)
            .collectLatest { exception: Exception? ->

                exception?.let {

                    shortToast(R.string.toast_algo_deu_errado)
                    setLayoutCarregando(false)
                    return@collectLatest

                }

                sharedAlunoViewModel.setDeveAtualizarDados(true)
                navegarUp()

            }
    }

    /**
     * Se a instância de alunoEmEdição não for nula, este método iniciara o processo de update de avatar do objeto
     * * Caso tenha sucesso, o fragment será fechado, caso não, o fragment fornecerá a opçao de tentar novamente.
     *
     * @see setLayoutCarregando
     */
    private suspend fun updateAluno() {

        val newAvatar = ContextCompat.getDrawable(requireContext(), avatarAtual)!!

        setLayoutCarregando(true)
        alunoViewModel.updateAluno(alunoEmEdicao!!.docId, newAvatar).collectLatest { exception ->

            exception?.let {
                setLayoutCarregando(false)
                shortToast(R.string.toast_algo_deu_errado)
            }
            sharedAlunoViewModel.setDeveAtualizarDados(true)
            navegarUp()
        }

    }

    /**
     * Define o estado atual do layout para carregando ou não carregando. Só sera colocado em estado de não carregando no inicio do fragment,
     * ou quando há uma falha no processo de backend.
     */
    private fun setLayoutCarregando(carregando: Boolean) {

        binder.fragmentAlunosAdicionarProgressSalvando.isVisible = carregando
        binder.fragmentAlunosAdicionarBtnSalvar.isVisible = !carregando
        binder.fragmentAlunosAdicionarContentCustomToolbar.contentAdicionarAlunoCustomToolbarImageVoltar.isEnabled =
            !carregando

    }

    /**
     * Faz a checagem de daddos de layout para verificar se todos os dados obrigatórios foram preenchidos.
     *
     * @return `true` se tudo estiver preenchido corretamente, `false` caso contário.
     */
    private fun podeSalvarAluno(): Boolean {

        return if (binder.fragmentAlunosAdicionarEditNome.text.toString().isBlank()) {
            shortToast(R.string.toast_salvar_crianca_preencha_nome)
            false
        } else if (!binder.fragmentAlunosAdicionarCheckBoxAutorizacao.isChecked) {
            shortToast(R.string.toast_salvar_crianca_politica_privacidade)
            false
        } else if(alunoEmEdicao != null && !trocouOAvatarDoAlunoEmEdicao){
            shortToast(R.string.toast_update_nao_trocou_avatar)
            false
        }else {
            true
        }

    }

    /**
     * Chama o onBackPressed() da activity host para fechar o fragment dentro do navigation scope.
     */
    private fun navegarUp() {
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedAlunoViewModel.setAlunoEdicao(null)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            binder.fragmentAlunosAdicionarContentCustomToolbar.contentAdicionarAlunoCustomToolbarImageVoltar.id -> navegarUp()
            binder.fragmentAlunosAdicionarBtnMudarAvatar.id -> mudarAvatar()
            binder.fragmentAlunosAdicionarBtnSalvar.id -> {

                lifecycleScope.launch {

                    if (!podeSalvarAluno()) return@launch

                    alunoEmEdicao?.let {
                        updateAluno()
                        return@launch
                    }
                    salvarAluno()
                }

            }
        }
    }


}