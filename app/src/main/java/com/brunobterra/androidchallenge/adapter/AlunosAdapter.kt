package com.brunobterra.androidchallenge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.brunobterra.androidchallenge.R
import com.brunobterra.androidchallenge.databinding.RowAlunosBinding
import com.brunobterra.androidchallenge.model.Aluno
import com.bumptech.glide.Glide

class AlunosAdapter(val context: Context, val callback : (Aluno) -> Unit) :
    PagingDataAdapter<Aluno, AlunosAdapter.AlunosViewHolder>(alunoModelComparador) {

    companion object {
        private val alunoModelComparador = object : DiffUtil.ItemCallback<Aluno>() {
            override fun areItemsTheSame(oldItem: Aluno, newItem: Aluno): Boolean {
                return oldItem.docId == newItem.docId
            }

            override fun areContentsTheSame(oldItem: Aluno, newItem: Aluno): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class AlunosViewHolder(val binder: RowAlunosBinding) :
        RecyclerView.ViewHolder(binder.root) {

        fun vincularInformacoes(aluno: Aluno) {
            binder.rowAlunosTxtNome.text = aluno.nome
            binder.rowAlunosTxtAno.text = context.getString(R.string.label_aluno_ano, aluno.ano)

            Glide.with(context).load(aluno.avatarUrl).into(binder.rowAlunosImageAvatar)

            binder.root.setOnClickListener{
                callback.invoke(aluno)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlunosViewHolder {
        return AlunosViewHolder(
            RowAlunosBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlunosViewHolder, position: Int) {
        val aluno = getItem(position)

        aluno?.let {
            holder.vincularInformacoes(it)
        }
    }

}