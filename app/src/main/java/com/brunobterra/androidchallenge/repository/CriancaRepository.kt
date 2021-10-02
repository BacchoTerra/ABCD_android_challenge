package com.brunobterra.androidchallenge.repository

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.brunobterra.androidchallenge.model.Crianca
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

class CriancaRepository {

    companion object {
        const val COLLECTION_CRIANCAS = "collection_criancas"
        const val STORAGE_REF_INICIAL = "criancas/avatar/"
    }

    private val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference


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

}