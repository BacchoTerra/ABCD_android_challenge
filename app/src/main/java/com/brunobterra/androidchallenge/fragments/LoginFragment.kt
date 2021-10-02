package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
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
import com.brunobterra.androidchallenge.databinding.FragmentLoginBinding
import com.brunobterra.androidchallenge.utils.shortToast
import com.brunobterra.androidchallenge.viewmodel.UsuarioViewModel
import com.brunobterra.androidchallenge.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Exception


class LoginFragment : Fragment(), View.OnClickListener{

    //Componentes de layout
    private val binder by lazy {
        FragmentLoginBinding.inflate(layoutInflater)
    }

    //ViewModels
    private val usuarioViewModel : UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((requireActivity().application as ChallengeApplication).usuarioRepo)
    }

    //Navigation
    lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{


        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binder.root)

        init()

    }

    private fun init() {

        binder.fragmentLoginBtnLogin.setOnClickListener(this)
        binder.fragmentLoginBtnEsqueciSenha.setOnClickListener(this)

    }

    private fun login() {

        val email = binder.fragmentLoginEditEmail.text.toString()
        val senha = binder.fragmentLoginEditSenha.text.toString()

        if (!dadosSaoValidos(email,senha)) {
            shortToast(R.string.toast_login_preencha_dados)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {

            setLayoutCarregando(true,null)

            usuarioViewModel.login(email,senha).collectLatest {response ->

                response.exception?.let { exception->

                    if (response.novoUsuario){
                        registrarUsuairio(email,senha)
                        return@collectLatest
                    }

                    setLayoutCarregando(false,exception)
                    return@collectLatest
                }

                navegarParaPaginaPrincipal()
            }
        }
    }

    private fun dadosSaoValidos(email:String,senha:String) : Boolean{

        return email.isNotBlank() && senha.isNotBlank()

    }

    private suspend fun registrarUsuairio(email: String,senha: String){

        usuarioViewModel.registrarUsuario(email,senha).collectLatest {e : Exception?->

            e?.let {exception->
                setLayoutCarregando(false,exception)
                return@collectLatest
            }

            navegarParaPaginaPrincipal()

        }

    }

    private suspend  fun setLayoutCarregando(carregando:Boolean,exception:Exception?) {

        withContext(Dispatchers.Main){
            binder.fragmentLoginProgressLogin.isVisible = carregando
            binder.fragmentLoginGroupBotoes.isVisible = !carregando

            exception?.let {
                shortToast(it.message)
            }

        }
    }

    private suspend fun navegarParaPaginaPrincipal() {

        withContext(Dispatchers.Main){
            navController.navigate(R.id.action_loginFragment_to_mainNavHostFragment)
        }
    }

    override fun onClick(p0: View?) {

        when(p0?.id) {

            binder.fragmentLoginBtnLogin.id -> login()

        }

    }

    override fun onResume() {
        super.onResume()

        usuarioViewModel.mFirebaseUser?.let {
            lifecycleScope.launch {
                navegarParaPaginaPrincipal()
            }
        }

    }

}