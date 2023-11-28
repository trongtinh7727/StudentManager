package com.app.studentmanagement.data.repository

import android.util.Log
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.LoginHistory
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.models.Student
import com.app.studentmanagement.services.DeleteUserRequest
import com.app.studentmanagement.services.UpdateUserRequest
import com.app.studentmanagement.services.UserService
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
        account.uid?.let {
            accountCollection.document(it).set(account)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
        }
    }

    fun getAccountByUid(uid: String, onComplete: (Account?) -> Unit) {
        val docRef = accountCollection.document(uid)

        val registration = docRef.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(null)
                return@addSnapshotListener
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val account = documentSnapshot.toObject(Account::class.java)
                onComplete(account)
            } else {
                onComplete(null)
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

    fun isEmailUnique(email: String, onComplete: (Boolean) -> Unit) {
        accountCollection.whereEqualTo("email", email).limit(1).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.isEmpty) // If no documents, the email is unique
            }
            .addOnFailureListener {
                onComplete(false) // Handle failure
            }
    }

    fun isCodelUnique(code: String, onComplete: (Boolean) -> Unit) {
        accountCollection.whereEqualTo("id", code).limit(1).get()
            .addOnSuccessListener { documents ->
                onComplete(documents.isEmpty) // If no documents, the email is unique
            }
            .addOnFailureListener {
                onComplete(false) // Handle failure
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

    fun updateUser(account: Account,newPass: String, onComplete: (Boolean) -> Unit) {
        val request = UpdateUserRequest(account.uid, account.email,newPass)
        userService.updateUser(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    accountCollection.document(account.uid).update("id",account.id,"email"
                        ,account.email, "name",account.name, "role",account.role)
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error", "onFailure: " + t.message)
                onComplete(false) // Handle failure
            }
        })

    }

    fun updateAvatar(userID: String, avatarUrl: String, onComplete: (Boolean) -> Unit) {
        val userRef = accountCollection.document(userID)
        userRef.update("avatarUrl",avatarUrl)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    fun addLoginHistory(userID: String, loginHistory: LoginHistory, onComplete: (Boolean) -> Unit) {
        val userRef = accountCollection.document(userID).collection("loginHistory")
        userRef.add(loginHistory)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }
    fun getLoginHistoryByID(userID: String,onComplete: (MutableList<LoginHistory>) -> Unit) {
       val loginCollection = accountCollection.document(userID).collection("loginHistory")
        val registration = loginCollection.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                onComplete(mutableListOf())
                return@addSnapshotListener
            }
            if (querySnapshot != null) {
                val loginHistories = mutableListOf<LoginHistory>()
                for (document in querySnapshot) {
                    val loginHistory = document.toObject(LoginHistory::class.java)
                    loginHistories.add(loginHistory)
                }
                onComplete(loginHistories)
            } else {
                onComplete(mutableListOf())
            }
        }
    }
}