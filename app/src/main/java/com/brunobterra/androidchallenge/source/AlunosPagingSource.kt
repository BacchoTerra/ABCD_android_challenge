package com.brunobterra.androidchallenge.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.brunobterra.androidchallenge.model.Aluno
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

/**
 * Classe base de paginação para leitura de objetos Aluno de dados do firestore .
 *
 *@param query uma query de firestore para busca de dados.
 *
 * @author Bruno B. Terra
 */
class AlunosPagingSource(val query : Query) : PagingSource<QuerySnapshot,Aluno>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Aluno>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Aluno> {

        return try {
            val paginaAtual = params.key?:query.get().await()
            //Gerar um ArrayOutBoundsException caso a busca nao tenha retornado resultados.
            val ultimoDoc = paginaAtual.documents[paginaAtual.documents.size -1]
            val proximaPagina = query.startAfter(ultimoDoc).get().await()

            LoadResult.Page(paginaAtual.toObjects(Aluno::class.java),null,proximaPagina)
        }catch (e:Exception){
            //Caso a exception seja ArrayOutOfBoundsException, significa que a pesquisa teve sucesso, mas não havia dados disponíveis.
            LoadResult.Error(e)
        }

    }
}