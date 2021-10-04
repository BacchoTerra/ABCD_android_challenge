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
import com.brunobterra.androidchallenge.databinding.FragmentForgotPasswordBinding
import com.brunobterra.androidchallenge.utils.shortToast
import com.brunobterra.androidchallenge.viewmodel.UsuarioViewModel
import com.brunobterra.androidchallenge.viewmodel.UsuarioViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ForgotPasswordFragment : Fragment(), View.OnClickListener{

    //layout components
    private val binder : FragmentForgotPasswordBinding by lazy {
        FragmentForgotPasswordBinding.inflate(layoutInflater)
    }

    //ViewModels
    private val usuarioViewModel : UsuarioViewModel by viewModels {
        UsuarioViewModelFactory((requireActivity().application as ChallengeApplication).usuarioRepo)
    }

    //NavController
    private lateinit var navController: NavController

    private var initialUserEmail : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retrieveInitialUserEmail()
    }

    private fun init() {

        navController = Navigation.findNavController(binder.root)

        binder.fragmentForgotPasswordBtnSendEmail.setOnClickListener(this)

    }

    private fun retrieveInitialUserEmail() {
        initialUserEmail = arguments?.getString(getString(R.string.args_navigation_forgot_password))

        initialUserEmail?.let {
            binder.fragmentForgotPasswordEditEmail.setText(it)
        }

    }

    private suspend fun sendEmail() {

        val email = binder.fragmentForgotPasswordEditEmail.text.toString()

        if (!email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            binder.fragmentForgotPasswordEditEmail.error = getString(R.string.edit_text_error_invalid_email)
            return
        }

        setLayoutToLoadState(true)

        usuarioViewModel.sendPasswordResetEmail(email).collectLatest {exception: Exception? ->

            exception?.let {
                shortToast(R.string.toast_algo_deu_errado)
                setLayoutToLoadState(false)
                return@collectLatest
            }

            shortToast(R.string.toast_check_your_email)
            requireActivity().onBackPressed()

        }
    }

    private fun setLayoutToLoadState(isLoading : Boolean) {

        binder.fragmentForgotPasswordBtnSendEmail.isVisible = !isLoading
        binder.fragmentForgotPasswordProgressSendingEmail.isVisible = isLoading

    }

    override fun onClick(p0: View?) {

        when(p0?.id) {

            binder.fragmentForgotPasswordBtnSendEmail.id -> {

                lifecycleScope.launch {
                    sendEmail()
                }
            }

        }

    }


}