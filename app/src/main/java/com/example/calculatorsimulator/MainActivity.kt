package com.example.calculatorsimulator
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorsimulator.R

import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private lateinit var displayText: TextView
    private var isNewOperation: Boolean = true
    private var lastResult: Double? = null
    private val decimalFormat = DecimalFormat("#.#")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayText = findViewById(R.id.result)

        val buttons = listOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3,
            R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7,
            R.id.button_8, R.id.button_9, R.id.button_Add,
            R.id.button_subtract, R.id.button_Multiple, R.id.button_Devide,
            R.id.button_dot, R.id.button_equal, R.id.button_C, R.id.button_CE,
            R.id.buttonBS, R.id.button_special
        )

        buttons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { onButtonClick(it as Button) }
        }
    }

    private fun onButtonClick(button: Button) {
        val buttonText = button.text.toString()

        when (buttonText) {
            "=" -> calculateResult()
            "C" -> clearAll()
            "CE" -> clearEntry()
            "BS" -> backspace()
            "+/-" -> toggleSign()
            else -> {
                if (isNewOperation) {
                    if (lastResult != null && isOperator(buttonText)) {
                        displayText.text = lastResult.toString()
                    } else if (lastResult != null) {
                        displayText.text = ""
                    }
                    isNewOperation = false
                }
                appendToDisplay(buttonText)
            }
        }
    }

    private fun appendToDisplay(text: String) {
        val modifiedText = if (text == "x") "*" else text
        displayText.append(modifiedText)
    }

    private fun calculateResult() {
        val expression = displayText.text.toString()

        if (expression.isEmpty() || isOperator(expression.last().toString())) {
            displayText.text = "Error"
            return
        }

        try {
            val result = ExpressionBuilder(expression).build().evaluate()
            val formattedResult = decimalFormat.format(result).toDouble()
            displayText.text = if (formattedResult % 1.0 == 0.0) {
                formattedResult.toInt().toString()
            } else {
                formattedResult.toString()
            }
            lastResult = formattedResult
            isNewOperation = true
        } catch (e: ArithmeticException) {
            displayText.text = "Error"
        } catch (e: Exception) {
            displayText.text = "Error"
        }
    }

    private fun toggleSign() {
        val currentText = displayText.text.toString()
        if (currentText.isNotEmpty() && currentText != "Error") {
            try {
                val currentNumber = currentText.toDouble()
                val newNumber = -currentNumber
                displayText.text = decimalFormat.format(newNumber)
            } catch (e: NumberFormatException) {
                displayText.text = "Error"
            }
        }
    }

    private fun clearEntry() {
        displayText.text = ""
    }

    private fun clearAll() {
        displayText.text = ""
        isNewOperation = true
        lastResult = null
    }

    private fun backspace() {
        val text = displayText.text.toString()
        if (text.isNotEmpty()) {
            displayText.text = text.dropLast(1)
        }
    }

    private fun isOperator(character: String): Boolean {
        return character == "+" || character == "-" || character == "*" || character == "/"
    }
}
