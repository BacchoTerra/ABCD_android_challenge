package com.brunobterra.androidchallenge.utils

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.shortToast(@StringRes resId: Int) {

    Toast.makeText(requireActivity(),resId,Toast.LENGTH_SHORT).show()
}

fun Fragment.shortToast(mensagem: String?) {

    mensagem?.let{
        Toast.makeText(requireActivity(),mensagem,Toast.LENGTH_SHORT).show()
    }
}