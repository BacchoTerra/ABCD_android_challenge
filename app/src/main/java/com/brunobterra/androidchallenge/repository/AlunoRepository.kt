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

class AlunoRepository {

    companion object {
        const val COLLECTION_ALUNOS = "collection_alunos"
        const val QUERY_LIMIT_ALUNOS = 10L
        const val STORAGE_REF_INICIAL = "alunos/avatar/"
    }

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    private val queryBasicaDeAluno: Query by lazy {
        mFirestore.collection(COLLECTION_ALUNOS).limit(QUERY_LIMIT_ALUNOS)
    }


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

    private suspend fun salvarAvatar(drawable: Drawable, docId: String): String {

        val avatarRef = mStorage.child("$STORAGE_REF_INICIAL/$docId/avatar.png")
        avatarRef.putBytes(getAvatarByteArray(drawable)).await()
        return avatarRef.downloadUrl.await().toString()
    }

    private fun getAvatarByteArray(drawable: Drawable): ByteArray {

        val bitmap = drawable.toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

        return baos.toByteArray()
    }

    fun getAlunos(query: Query): LiveData<PagingData<Aluno>> {

        return Pager(config = PagingConfig(QUERY_LIMIT_ALUNOS.toInt()), pagingSourceFactory = {
            AlunosPagingSource(query)
        }).liveData
    }

    suspend fun updateAluno(docId: String, newAvatar : Drawable): Exception? {

        return try {

            val avatarUrl = salvarAvatar(newAvatar,docId)
            mFirestore.collection(COLLECTION_ALUNOS).document(docId).update(Aluno::avatarUrl.name,avatarUrl).await()
            null
        } catch (e: Exception) {
            e
        }
    }

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

data class AlunoQueryBuilder(
    var orderBy: AlunoQuery = AlunoQuery.ORDER_NAME,
    var nameQuery: String? = null
)

enum class AlunoQuery {
    ORDER_NAME,
    ORDER_ANO
}