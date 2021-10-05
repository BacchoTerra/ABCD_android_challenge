package com.brunobterra.androidchallenge.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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

        init()

    }

    /**
     * Inicial variaveis principais da classe e clickListeners.
     */
    private fun init() {

        navController = Navigation.findNavController(binder.root)

        binder.fragmentLoginBtnLogin.setOnClickListener(this)
        binder.fragmentLoginBtnEsqueciSenha.setOnClickListener(this)

    }

    /**
     * Inicia a operação de login do usuario. Caso ela falhe por um erro de `FirebaseAuthInvalidUserException`,
     * significando que o usuario não esta registrado, ela delegará a função de registrar para a função
     * `registrarUsuario`. Caso seja um erro diferente, notificara o usuario atraves de um Toast.
     *
     * @see registrarUsuairio
     */
    private fun login() {

        val email = binder.fragmentLoginEditEmail.text.toString()
        val senha = binder.fragmentLoginEditSenha.text.toString()

        if (!dadosSaoValidos(email,senha)) return


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

    /**
     * Checa se os campos de não foram deixados em branco.
     *
     * @param email E-mail do usuário.
     * @param senha senha do usuário.
     * @return `true` quando todos os campos foram preenchidos, `false` quando pelo menos um não for.
     */
    private fun dadosSaoValidos(email:String,senha:String) : Boolean{

        if (email.isNotBlank() && senha.isNotBlank())
            return true


        if (email.isBlank()) {
            binder.fragmentLoginInputLayoutEmail.error = getString(R.string.edit_text_error_campo_obrigatorio)
        }

        if (senha.isBlank()) {
            binder.fragmentLoginInputLayoutSenha.error = getString(R.string.edit_text_error_campo_obrigatorio)
        }

        return false
    }

    /**
     * Caso o método `login` retorne `FirebaseAuthInvalidUserException` esse método será chamado para registrar o usuario.
     *
     * @param email E-mail do usuário.
     * @param senha senha do usuário.
     */
    private suspend fun registrarUsuairio(email: String,senha: String){

        usuarioViewModel.registrarUsuario(email,senha).collectLatest {e : Exception?->

            e?.let {exception->
                setLayoutCarregando(false,exception)
                return@collectLatest
            }

            navegarParaPaginaPrincipal()

        }

    }

    /**
     * Coloca o layout em estado de carregamento para informar o usuário sobre a operação de background.
     * caso a operação de background tenha falhado, exibe uma mensagem Toast com a exception.
     *
     * @param carregando define as visibilidades de views de carregamento.
     * @param exception se não null, fornecerá uma mensagem para demonstrar ao usuário.
     *
     * @see login
     * @see registrarUsuairio
     */
    private suspend  fun setLayoutCarregando(carregando:Boolean,exception:Exception?) {

        withContext(Dispatchers.Main){
            binder.fragmentLoginProgressLogin.isVisible = carregando
            binder.fragmentLoginGroupBotoes.isVisible = !carregando

            exception?.let {
                shortToast(it.message)
            }

        }
    }

    private fun esqueceuSenha() {

        val emailParcial = binder.fragmentLoginEditEmail.text.toString()

        navController.navigate(R.id.action_loginFragment_to_forgotPasswordFragment,
        bundleOf(getString(R.string.args_navigation_esqueci_senha) to emailParcial))

    }

    /**
     * Realiza a troca de fragment para a tela principal somente se a operação de login,
     * registrar usuario, ou caso ja tenha um usuario logado sejam verdadeiras e tenham sucesso.
     */
    private suspend fun navegarParaPaginaPrincipal() {

        withContext(Dispatchers.Main){
            navController.navigate(R.id.action_loginFragment_to_mainNavHostFragment)
        }
    }

    override fun onClick(p0: View?) {

        when(p0?.id) {

            binder.fragmentLoginBtnLogin.id -> login()
            binder.fragmentLoginBtnEsqueciSenha.id -> esqueceuSenha()

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