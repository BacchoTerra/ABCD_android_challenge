package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.application.ChallengeApplication
import com.brunobterra.androidchallenge.databinding.FragmentEsqueciSenhaBinding
import com.brunobterra.androidchallenge.utils.shortToast
import com.brunobterra.androidchallenge.viewmodel.UsuarioViewModel
import com.brunobterra.androidchallenge.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EsqueciSenhaFragment : Fragment(), View.OnClickListener {

    //Componentes de layout
    private val binder: FragmentEsqueciSenhaBinding by lazy {
        FragmentEsqueciSenhaBinding.inflate(layoutInflater)
    }

    //ViewModels
    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((requireActivity().application as ChallengeApplication).usuarioRepo)
    }

    //NavController
    private lateinit var navController: NavController

    //Só sera recuperado caso o usuário ja tenha digitado seu e-mail previamente.
    private var emailPrevioDoUsuario: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        recuperarEmailPrevioDoUsuario()
    }

    /**
     * Faz todas as operação necessárias para o funcionamento da UI do fragment.
     */
    private fun init() {

        navController = Navigation.findNavController(binder.root)

        binder.fragmentEsqueciSenhaBtnEnviarEmail.setOnClickListener(this)

    }

    /**
     * Se o usuario já tiver digitado seu email em uma caixa de texto prévia entrada deste fragment, ele será recuperado
     * e automaticamente vinculado ao layout atual.
     */
    private fun recuperarEmailPrevioDoUsuario() {
        emailPrevioDoUsuario =
            arguments?.getString(getString(R.string.args_navigation_esqueci_senha))

        emailPrevioDoUsuario?.let {
            binder.fragmentEsqueciSenhaEditEmail.setText(it)
        }

    }

    /**
     * Inicia o processo de envio de email de reset de senha do usuário.
     */
    private suspend fun enviarEmail() {

        val email = binder.fragmentEsqueciSenhaEditEmail.text.toString()

        if (!email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            binder.fragmentEsqueciSenhaInputLayoutEmail.error =
                getString(R.string.edit_text_erro_email_invalido)
            return
        }

        setEstadoDeCarregamentoDoLayout(true)

        usuarioViewModel.enviarEmailDeResetDeSenha(email).collectLatest { exception: Exception? ->

            exception?.let {
                shortToast(R.string.toast_algo_deu_errado)
                setEstadoDeCarregamentoDoLayout(false)
                return@collectLatest
            }

            shortToast(R.string.toast_cheque_seu_email)
            requireActivity().onBackPressed()

        }
    }

    /**
     * Define o estado atual do layout para carregando ou não carregando. Só sera colocado em estado de não carregando quando há uma falha no
     * processo do backend.
     *
     * @param um boolean que controla o estado de carregamento do layout.
     */
    private fun setEstadoDeCarregamentoDoLayout(carregando: Boolean) {

        binder.fragmentEsqueciSenhaBtnEnviarEmail.isVisible = !carregando
        binder.fragmentEsqueciSenhaProgressEnviandoEmail.isVisible = carregando

    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            binder.fragmentEsqueciSenhaBtnEnviarEmail.id -> {

                lifecycleScope.launch {
                    enviarEmail()
                }
            }

        }

    }


}