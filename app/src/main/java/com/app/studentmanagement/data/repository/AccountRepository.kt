package com.app.studentmanagement.data.repository

import android.util.Log
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.services.DeleteUserRequest
import com.app.studentmanagement.services.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AccountRepository {
    private val db = FirebaseFirestore.getInstance()
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://138.2.68.228:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val accountCollection = db.collection("accounts")
    private val userService = retrofit.create(UserService::class.java)

    fun addAccount(account: Account, onComplete: (Boolean) -> Unit) {
        account.id?.let {
            accountCollection.document(it).set(account)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
        }
    }

    fun getAccountByRole(role: Role, onComplete: (List<Account>) -> Unit){

        val query = if (role == Role.All) {
            accountCollection
        } else {
            accountCollection.whereEqualTo("role", role)
        }

        val registration = query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(emptyList())
                return@addSnapshotListener
            }

            if (querySnapshot != null) {
                val accounts = mutableListOf<Account>()
                for (document in querySnapshot) {
                    val account = document.toObject(Account::class.java)
                    accounts.add(account)
                }
                onComplete(accounts)
            } else {
                onComplete(emptyList())
            }
        }
    }

    fun updateAccount(account: Account, onComplete: (Boolean) -> Unit) {

        account.id?.let {
            accountCollection.document(it).set(account, SetOptions.merge())
                .addOnSuccessListener {
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        }
    }

    fun deleteUser(uidToDelete: String, onComplete: (Boolean) -> Unit) {
        val request = DeleteUserRequest(uidToDelete)

        userService.deleteUser(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    accountCollection.document(uidToDelete).delete()
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener{
                            onComplete(false)
                        }
                } else {
                    onComplete(false) // Handle deletion error
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.i("Errr", "onFailure: "+ t.message)
                onComplete(false) // Handle failure
            }
        })
    }
}