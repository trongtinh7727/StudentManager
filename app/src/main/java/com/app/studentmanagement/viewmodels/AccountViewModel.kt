package com.app.studentmanagement.viewmodels

import androidx.lifecycle.ViewModel
import com.app.studentmanagement.data.models.Account

class AccountViewModel : ViewModel() {
    fun generateAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()

        for (i in 1..30) {
            val id = i.toString()
            val name = "User $i"
            val role = if (i % 2 == 0) "Admin" else "User"

            val account = Account(id, name, role)
            accounts.add(account)
        }

        return accounts
    }
}