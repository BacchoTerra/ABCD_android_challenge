package com.brunobterra.androidchallenge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.RowCriancasBinding
import com.brunobterra.androidchallenge.model.Crianca
import com.bumptech.glide.Glide

class CriancasAdapter(val context: Context) :
    PagingDataAdapter<Crianca, CriancasAdapter.CriancasViewHolder>(criancaComparator) {

    companion object {
        private val criancaComparator = object : DiffUtil.ItemCallback<Crianca>() {
            override fun areItemsTheSame(oldItem: Crianca, newItem: Crianca): Boolean {
                return oldItem.docId == newItem.docId
            }

            override fun areContentsTheSame(oldItem: Crianca, newItem: Crianca): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class CriancasViewHolder(val binder: RowCriancasBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun bind(crianca: Crianca) {
            binder.rowCriancaTxtNome.text = crianca.nome
            binder.rowCriancaTxtAno.text = context.getString(R.string.label_aluno_ano, crianca.ano)

            Glide.with(context).load(crianca.avatarUrl).into(binder.rowCriancaImageAvatar)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CriancasViewHolder {
        return CriancasViewHolder(
            RowCriancasBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CriancasViewHolder, position: Int) {
        val crianca = getItem(position)

        crianca?.let {
            holder.bind(it)
        }
    }

}