package com.app.studentmanagement.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.repository.AccountRepository
import com.google.firebase.auth.FirebaseAuth

class AccountViewModel : ViewModel() {
    private val  accountRepository = AccountRepository()



    private val _loadingIndicator = MutableLiveData<Boolean>()
    val loadingIndicator: LiveData<Boolean>
        get() = _loadingIndicator

    private val _currentUser = MutableLiveData<Account>()
    val  currentUser: LiveData<Account>
        get() = _currentUser


    private val _accounts = MutableLiveData<List<Account>>()
    val  accounts: LiveData<List<Account>>
        get() = _accounts

    fun getAccounts(role: Role){
        _loadingIndicator.value = true
        accountRepository.getAccountByRole(role){
            if (it.isNotEmpty()){
                _accounts.postValue(it)
            }
            _loadingIndicator.postValue(false)
        }
    }

    fun setCurrentUser(){
        _loadingIndicator.value = true
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            accountRepository.getAccountByUid(uid){
                if (it != null){
                    _currentUser.postValue(it)
                }
                _loadingIndicator.postValue(false)
            }
        }
    }
    fun deleteAccount(account: Account){
        _loadingIndicator.value = true
        accountRepository.deleteUser(account.uid){
            _loadingIndicator.postValue(false)
        }
    }

    fun updateAccount(account: Account,password: String, onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true
        accountRepository.updateUser(account,password){
                success ->
            if (success) {
                _loadingIndicator.postValue(false)
                onComplete(true)
            }else{
                _loadingIndicator.postValue(false)
                onComplete(false)
            }
        }
    }

    fun isEmailUnique(email: String,onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true
        accountRepository.isEmailUnique(email){
            onComplete(it)
            _loadingIndicator.postValue(false)
        }
    }
    fun isCodeUnique(id: String,onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true
        accountRepository.isCodelUnique(id){
            onComplete(it)
            _loadingIndicator.postValue(false)
        }
    }

    fun login(emailValue: String, passwordValue:String, onComplete: (Boolean) -> Unit) {
        _loadingIndicator.value = true
        if (!emailValue.isNullOrEmpty() && !passwordValue.isNullOrEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener { task ->
                    onComplete(true)
                    _loadingIndicator.value = false
                }
        } else {
            onComplete(false)
            _loadingIndicator.value = false
        }
    }


    fun createAccount(account: Account,  password:String, onComplete: (Boolean) -> Unit){
        _loadingIndicator.value = true

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(account.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    if (firebaseUser != null) {
                        account.uid = firebaseUser.uid
                        accountRepository.addAccount(account) { success ->
                            if (success) {
                                _loadingIndicator.postValue(false)
                                onComplete(true)
                            }else{
                                _loadingIndicator.postValue(false)
                                onComplete(false)
                            }
                        }
                    }
                } else {
                    _loadingIndicator.postValue(false)
                    val exception = task.exception
                    if (exception != null) {
                    }
                }
            }
    }
}