package com.app.studentmanagement.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.studentmanagement.data.models.Account
import com.app.studentmanagement.data.models.Role
import com.app.studentmanagement.data.repository.AccountRepository

class AccountViewModel : ViewModel() {
    private val  accountRepository = AccountRepository()

    private val _loadingIndicator = MutableLiveData<Boolean>()
    val loadingIndicator: LiveData<Boolean>
        get() = _loadingIndicator

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
    fun deleteAccount(account: Account){
        _loadingIndicator.value = true
        accountRepository.deleteUser(account.id){
            if (it){

            }
            _loadingIndicator.postValue(false)
        }
    }
    fun generateAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()

        for (i in 1..20) {
            val id = i.toString()
            val name = "User $i"
            val role = if (i % 2 == 0) "Admin" else "User"

            val account = Account(id, name, role)
            accounts.add(account)
        }
        return accounts
    }
}