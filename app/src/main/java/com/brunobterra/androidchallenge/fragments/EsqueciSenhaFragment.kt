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


class EsqueciSenhaFragment : Fragment(), View.OnClickListener{

    //Componentes de layout
    private val binder : FragmentEsqueciSenhaBinding by lazy {
        FragmentEsqueciSenhaBinding.inflate(layoutInflater)
    }

    //ViewModels
    private val usuarioViewModel : UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((requireActivity().application as ChallengeApplication).usuarioRepo)
    }

    //NavController
    private lateinit var navController: NavController

    private var emailPrevioDoUsuario : String? = null

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

    private fun init() {

        navController = Navigation.findNavController(binder.root)

        binder.fragmentEsqueciSenhaBtnEnviarEmail.setOnClickListener(this)

    }

    private fun recuperarEmailPrevioDoUsuario() {
        emailPrevioDoUsuario = arguments?.getString(getString(R.string.args_navigation_esqueci_senha))

        emailPrevioDoUsuario?.let {
            binder.fragmentEsqueciSenhaEditEmail.setText(it)
        }

    }

    private suspend fun enviarEmail() {

        val email = binder.fragmentEsqueciSenhaEditEmail.text.toString()

        if (!email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            binder.fragmentEsqueciSenhaInputLayoutEmail.error = getString(R.string.edit_text_erro_email_invalido)
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

    private fun setEstadoDeCarregamentoDoLayout(carregando : Boolean) {

        binder.fragmentEsqueciSenhaBtnEnviarEmail.isVisible = !carregando
        binder.fragmentEsqueciSenhaProgressEnviandoEmail.isVisible = carregando

    }

    override fun onClick(p0: View?) {

        when(p0?.id) {

            binder.fragmentEsqueciSenhaBtnEnviarEmail.id -> {

                lifecycleScope.launch {
                    enviarEmail()
                }
            }

        }

    }


}