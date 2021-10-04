package com.brunobterra.androidchallenge.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.brunobterra.androidchallenge.model.Crianca
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class AlunosPagingSource(val query : Query) : PagingSource<QuerySnapshot,Crianca>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Crianca>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Crianca> {

        return try {
            val paginaAtual = params.key?:query.get().await()
            val ultimoDoc = paginaAtual.documents[paginaAtual.documents.size -1]
            val proximaPagina = query.startAfter(ultimoDoc).get().await()

            LoadResult.Page(paginaAtual.toObjects(Crianca::class.java),null,proximaPagina)
        }catch (e:Exception){
            LoadResult.Error(e)
        }

    }
}