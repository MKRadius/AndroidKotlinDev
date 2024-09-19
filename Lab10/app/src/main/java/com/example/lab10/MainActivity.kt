package com.example.lab10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lab10.ui.theme.Lab10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab10Theme {
                GameScreen()
            }
        }
    }
}

@Composable
fun GameScreen() {
    val numRange: IntRange = 1..10
    var numGame by remember { mutableStateOf(NumberGame(numRange)) }
    var userInput by remember { mutableStateOf("") }
    var validInput by remember { mutableStateOf(false) }

    Column {
        Spacer(Modifier.padding(16.dp))
        Text("Hello! Guess a number in ${numRange.first}..${numRange.last}")
        TextField(
            value = userInput,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                    userInput = newValue
                    validInput = false
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
        )
        Button(
            onClick = { userInput.toIntOrNull()?.let { validInput = true } ?: run { validInput = false } },
            modifier = Modifier.height(40.dp)
        ) {
            Text("Make Guess!")
        }
        if (validInput) Text("${numGame.makeGuess(userInput.toInt())} [${numRange.first}, ${numRange.last}]")
    }
}

enum class GuessResult {HIGH, LOW, HIT}

class NumberGame(val range: IntRange) {
    private val secret = range.random()
    var guesses = listOf<Int>()
        private set

    fun makeGuess(guess: Int): GuessResult {
        guesses = guesses.plus(guess)
        return when (guess) {
            in Int.MIN_VALUE..<secret -> GuessResult.LOW
            in secret+1..Int.MAX_VALUE -> GuessResult.HIGH
            else -> GuessResult.HIT
        }
    }
}

