package com.brunobterra.androidchallenge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.RowCriancasBinding
import com.brunobterra.androidchallenge.model.Crianca

class CriancasAdapter(val context:Context) : RecyclerView.Adapter<CriancasAdapter.CriancasViewHolder>() {

    val list = mutableListOf<Crianca>()
    init {
        for(i in 0..40){
            list.add(Crianca("Bruno Baccho Terra"))
        }
    }

    inner class CriancasViewHolder(val binder: RowCriancasBinding) : RecyclerView.ViewHolder(binder.root) {

        fun bind(crianca: Crianca){
            binder.rowCriancaTxtNome.text = crianca.nome
            binder.rowCriancaTxtAno.text = context.getString(R.string.label_aluno_ano,crianca.ano)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriancasViewHolder {
        return CriancasViewHolder(RowCriancasBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: CriancasViewHolder, position: Int) {
        val crianca = list[position]

        holder.bind(crianca)

    }

    override fun getItemCount() = list.size

}