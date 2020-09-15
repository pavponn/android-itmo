package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.udojava.evalex.Expression
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.math.MathContext

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val EXPR_KEY = "EXPR_KEY"
        const val RES_KEY = "RES_KEY"
    }
    private var expression: String = ""
    private var result: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        result = getString(R.string.init_text_calc)
        Log.d(TAG, "onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
        outState.putString(EXPR_KEY, expression)
        outState.putString(RES_KEY, result)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstance")
        expression = savedInstanceState.getString(EXPR_KEY, "")
        result = savedInstanceState.getString(RES_KEY, getString(R.string.init_text_calc))
        syncStateWithUI()
    }


    fun onButtonClick(v: View) {
        val button: Button = v as Button

        when (button.id) {
            clearAllButton.id -> {
                expression = ""
                result = getString(R.string.init_text_calc)
            }
            clearButton.id -> {
                if (expression.isNotEmpty()) {
                    expression = expression.substring(0, expression.length - 1)
                }
            }
            eqButton.id -> {
                result = try {
                    // Calculates expression according to IEEE 754, 16 digit precision
                    Expression(expression, MathContext.DECIMAL64).eval().toPlainString()
                } catch (e: Exception) {
                    Log.w(TAG, "Error while calculating expression.")
                    "Error!"
                }
            }
            else -> {
                expression += button.text
            }
        }
        syncStateWithUI()

        expressionView.post {
            scrollExpressionView.fullScroll(View.FOCUS_RIGHT)
        }
        resultView.post {
            scrollResultView.fullScroll(View.FOCUS_RIGHT)
        }
    }

    private fun syncStateWithUI() {
        if (expression.isNotEmpty()) {
            expressionView.text = expression
        } else {
            expressionView.text = getString(R.string.init_text_calc)
        }
        resultView.text = result
    }

}