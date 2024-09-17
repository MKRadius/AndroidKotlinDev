package com.example.lab07

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab07.ui.theme.Lab07Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

const val N = 100
class MainActivity : ComponentActivity() {
    data class Saldos(val saldo1: Double, val saldo2: Double)
    class Account {
        private var amount: Double = 0.0
        private val mutex = Mutex()

        suspend fun deposit(amount: Double) {
            mutex.withLock {
                val x = this.amount
                delay(1) // simulates processing time
                this.amount = x + amount
            }
        }
        suspend fun saldo(): Double = mutex.withLock { this.amount }
    }

    /* Approximate measurement of the given block's execution time */
    fun withTimeMeasurement(title:String, isActive:Boolean=true, code:() -> Unit) {
        if(!isActive) return
        val time = measureTimeMillis { code() }
        Log.i("MSU", "operation in '$title' took $time ms")
    }

    fun bankProcess(account: Account): Saldos {
        var saldo1: Double = 0.0
        var saldo2: Double = 0.0

        /* we measure the execution time of one deposit task */
        withTimeMeasurement("Single coroutine deposit $N times") {
            runBlocking {
                launch {
                    for (i in 1..N)
                        account.deposit(0.0)
                }.join()
                saldo1 = account.saldo()
            }
        }

        /* then we measure the execution time of two simultaneous deposit tasks using coroutines */
        withTimeMeasurement("Two $N times deposit coroutines together", isActive = true) {
            runBlocking {
                launch {
                    for (i in 1..N) account.deposit(1.0)
                }.join()
                launch {
                    for (i in 1..N) account.deposit(1.0)
                }.join()
                saldo2 = account.saldo()
            }
        }
        return Saldos(saldo1, saldo2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val results = bankProcess(Account())
        setContent {
            Lab07Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowResults(saldo1 = results.saldo1, saldo2 = results.saldo2)
                }
            }
        }
    }
}

@Composable
fun ShowResults(saldo1: Double, saldo2: Double) {
    Column {
        Text(text = "Saldo1: $saldo1")
        Text(text = "Saldo2: $saldo2")
    }
}
