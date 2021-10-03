package com.brunobterra.androidchallenge.repository

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.paging.*
import com.brunobterra.androidchallenge.model.Crianca
import com.brunobterra.androidchallenge.source.AlunosPagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

class CriancaRepository {

    companion object {
        const val COLLECTION_CRIANCAS = "collection_criancas"
        const val QUERY_LIMIT_ALUNOS = 10L
        const val STORAGE_REF_INICIAL = "criancas/avatar/"
    }

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    private val baseAlunoQuery: Query by lazy {
        mFirestore.collection(COLLECTION_CRIANCAS).limit(QUERY_LIMIT_ALUNOS)
    }


    suspend fun salvarCrianca(crianca: Crianca, avatar: Drawable): Exception? {

        val docId = UUID.randomUUID().toString()
        return try {
            val avatarUrl = salvarAvatar(avatar, docId)

            crianca.docId = docId
            crianca.avatarUrl = avatarUrl

            mFirestore.collection(COLLECTION_CRIANCAS).document(docId).set(crianca).await()
            null
        } catch (e: Exception) {
            e
        }
    }

    private suspend fun salvarAvatar(drawable: Drawable, docId: String): String {

        val avatarRef = mStorage.child("$STORAGE_REF_INICIAL/$docId/avatar.jpeg")
        avatarRef.putBytes(getAvatarByteArray(drawable)).await()
        return avatarRef.downloadUrl.await().toString()
    }

    private fun getAvatarByteArray(drawable: Drawable): ByteArray {

        val bitmap = drawable.toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

        return baos.toByteArray()
    }

    fun getCriancas(query: Query): LiveData<PagingData<Crianca>> {

        return Pager(config = PagingConfig(10), pagingSourceFactory = {
            AlunosPagingSource(query)
        }).liveData
    }

    fun defineQuery(orderBy: AlunoQuery, nameQuery: String?): Query {

        var definedQuery: Query = baseAlunoQuery

        if (orderBy == AlunoQuery.ORDER_NAME) {
            definedQuery =
                baseAlunoQuery.orderBy(Crianca::nomeDePesquisa.name, Query.Direction.ASCENDING)

            nameQuery?.let {
                definedQuery =
                    definedQuery.whereGreaterThanOrEqualTo(Crianca::nomeDePesquisa.name, it)
            }

        } else if (orderBy == AlunoQuery.ORDER_ANO) {

            definedQuery = baseAlunoQuery.orderBy(Crianca::ano.name, Query.Direction.ASCENDING)
        }

        return definedQuery

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