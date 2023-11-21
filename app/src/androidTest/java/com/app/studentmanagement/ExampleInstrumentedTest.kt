package com.app.studentmanagement

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.studentmanagement.data.repository.AccountRepository

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val accountRepository = AccountRepository()
    @Test
    fun useAppContext() {
        // Context of the app under test.
        accountRepository.deleteUser("Ju48UYrGFaZlZHrBNmW2APlhskz1"){
            assert(it)
        }
        Thread.sleep(5000)
    }

    @Test
    @Throws(Exception::class)
    fun testAddUser(){
        accountRepository.deleteUser("6bDNiUapo5SZqRcuTzjQ3pBz8Nq2"){
            assert(it)
        }
        Thread.sleep(1000)

    }

}