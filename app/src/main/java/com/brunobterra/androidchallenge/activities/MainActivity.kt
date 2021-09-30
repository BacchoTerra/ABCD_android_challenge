package com.brunobterra.androidchallenge.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.brunobterra.androidchallenge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Layout components
    private val binder by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binder.root)
    }
}