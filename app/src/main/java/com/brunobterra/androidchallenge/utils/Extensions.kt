package com.brunobterra.androidchallenge.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

/**
 * Cria um toast a partir de um string resource
 *
 * @param resId resource id de uma string para a mensagem do Toast.
 */
fun Fragment.shortToast(@StringRes resId: Int) {

    Toast.makeText(requireActivity(),resId,Toast.LENGTH_SHORT).show()
}

/**
 * Cria um toast a partir de uma String.
 * Não deve ser usado em produção. Para produção deve-se usar um string resource.
 *
 * @param mensagem Uma string para ser mostrada em um Toast.
 *
 * @see shortToast
 */
fun Fragment.shortToast(mensagem: String?) {

    mensagem?.let{
        Toast.makeText(requireActivity(),mensagem,Toast.LENGTH_SHORT).show()
    }
}