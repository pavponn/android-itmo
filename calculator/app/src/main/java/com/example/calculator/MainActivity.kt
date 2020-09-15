package com.example.calculator

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
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
    private var result: String = "";


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        result = getString(R.string.init_text_calc)
        expression = getString(R.string.init_text_calc)
        Log.d(TAG, "onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState")
        outState.putString(EXPR_KEY, expressionView.text.toString())
        outState.putString(RES_KEY, resultView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d(TAG, "onRestoreInstance")
        super.onRestoreInstanceState(savedInstanceState)
        expressionView.text = savedInstanceState.getString(EXPR_KEY, "")
        resultView.text = savedInstanceState.getString(RES_KEY, "")
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
                    Log.d(TAG, "Error while calculating expression.")
                    "Error!"
                }
            }
            else -> {
                expression += button.text
            }
        }
        if (expression.isNotEmpty()) {
            expressionView.text = expression
        } else {
            expressionView.text = getString(R.string.init_text_calc)
        }
        resultView.text = result

        expressionView.post {
            scrollExpressionView.fullScroll(View.FOCUS_RIGHT)
        }
        resultView.post {
            scrollResultView.fullScroll(View.FOCUS_RIGHT)
        }
    }

}