package com.brunobterra.androidchallenge.repository

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.brunobterra.androidchallenge.model.Aluno
import com.brunobterra.androidchallenge.source.AlunosPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * Classe responsavel por toda interação entre o aplicativo com o backend.
 *
 * @author Bruno B. Terra
 */

class AlunoRepository {

    companion object {
        const val COLLECTION_ALUNOS = "collection_alunos"
        const val QUERY_LIMIT_ALUNOS = 10L
        const val STORAGE_REF_INICIAL = "alunos/avatar/"
    }

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    //Toda query para o firestore deve uasr essa query como base
    private val queryBasicaDeAluno: Query by lazy {
        mFirestore.collection(COLLECTION_ALUNOS).limit(QUERY_LIMIT_ALUNOS)
    }


    /**
     * Salva um objeto de Aluno no firebase firestore após salvar o avatar no cloud storage;
     *
     * @param aluno um objeto de aluno.
     * @param avatar um drawable que tera seu url salvo em aluno.
     * @return uma exception caso o upload tenha falhado. Null em caso de sucesso.
     *
     * @see salvarAvatar
     */
    suspend fun salvarAluno(aluno: Aluno, avatar: Drawable): Exception? {

        val docId = UUID.randomUUID().toString()
        return try {
            val avatarUrl = salvarAvatar(avatar, docId)

            aluno.docId = docId
            aluno.avatarUrl = avatarUrl

            mFirestore.collection(COLLECTION_ALUNOS).document(docId).set(aluno).await()
            null
        } catch (e: Exception) {
            e
        }
    }

    /**
     * Salva um byteArray de um drawable no cloud storage no formato PNG.
     *
     * @param drawable um drawable para ser salvo.
     * @param docId id de um documento do firestore para ser usado como folder da imagem salva.
     * @return uma string contendo o URL do local onde a imagem foi salva.
     */
    private suspend fun salvarAvatar(drawable: Drawable, docId: String): String {

        val avatarRef = mStorage.child("$STORAGE_REF_INICIAL/$docId/avatar.png")
        avatarRef.putBytes(getAvatarByteArray(drawable)).await()
        return avatarRef.downloadUrl.await().toString()
    }

    /**
     * Gera um byteArray de um drawable para possibilitar salvar a image no cloud storage.
     *
     * @param drawable um drawable para ser passado para byteArray.
     * @return um byteArray do drawable passado como paramêtro.
     */
    private fun getAvatarByteArray(drawable: Drawable): ByteArray {

        val bitmap = drawable.toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

        return baos.toByteArray()
    }

    /**
     * Faz a busca de dados do firestore usando a query passada como parâmetro.
     *
     * @param query uma firestore query para busca de dados.
     * @return Uma liveData contendo uma PagingData dos dados recebidos do firestore.
     *
     * @see definirNovaQuery
     */
    fun getAlunos(query: Query): LiveData<PagingData<Aluno>> {

        return Pager(config = PagingConfig(QUERY_LIMIT_ALUNOS.toInt()), pagingSourceFactory = {
            AlunosPagingSource(query)
        }).liveData
    }

    /**
     * Faz uma operação de update de imagem de um Auluno salvo no cloud storage e do url dela no firestore no dado id de documento.
     *
     * @param docId uma string contendo o id do documento no firestore, que é o mesmo que o folder onde a image está salva no cloud storage.
     * @return uma Exception? caso algo tenha falhado no upload de imagem ou update de field no firestore. Null caso contrário.
     *
     * @see salvarAvatar
     */
    suspend fun updateAluno(docId: String, newAvatar: Drawable): Exception? {

        return try {

            val avatarUrl = salvarAvatar(newAvatar, docId)
            mFirestore.collection(COLLECTION_ALUNOS).document(docId)
                .update(Aluno::avatarUrl.name, avatarUrl).await()
            null
        } catch (e: Exception) {
            e
        }
    }

    /**
     * Responsavel pela construção de uma query para busca de dados no firestore.
     * Esse método sempre deve ser chamado antes de uma nova busca no caso de mudança de dados da pesquisa.
     *
     * @param builder um objeto de AlunoQueryBuilder contento informações sobre como ordenar e como filtrar a query.
     * @return uma nova query para ser usada em uma nova pesquisa do firestore.
     *
     * @see getAlunos
     */
    fun definirNovaQuery(builder: AlunoQueryBuilder): Query {

        var novaQuery: Query = queryBasicaDeAluno

        if (builder.orderBy == AlunoQuery.ORDER_NAME) {
            novaQuery =
                queryBasicaDeAluno.orderBy(Aluno::nomeDePesquisa.name, Query.Direction.ASCENDING)

            builder.nameQuery?.let {
                novaQuery =
                    novaQuery.whereGreaterThanOrEqualTo(Aluno::nomeDePesquisa.name, it)
            }

        } else if (builder.orderBy == AlunoQuery.ORDER_ANO) {

            novaQuery = queryBasicaDeAluno.orderBy(Aluno::ano.name, Query.Direction.ASCENDING)
        }

        return novaQuery

    }
}

/**
 * Helper class para construir uma query do firestore.
 * Ela possui atributos que fazem referêncaia sobre como os dados devem ser ordenados filtrados em uma pesquisa.
 *
 * @author Bruno B.Terra
 *
 */
data class AlunoQueryBuilder(
    var orderBy: AlunoQuery = AlunoQuery.ORDER_NAME,
    var nameQuery: String? = null
)

/**
 * Classe que deve ser usada em conjuto com AlunoQueryBuilder para definir como os dados do firestore devem ser ordenados.
 *
 */
enum class AlunoQuery {
    ORDER_NAME,
    ORDER_ANO
}