package com.example.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.weather.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val THEME_KEY = "THEME_KEY"
    }

    enum class Theme { LIGHT, DARK }

    private var currentTheme = Theme.LIGHT
    private lateinit var mainActivityBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        currentTheme = savedInstanceState?.getSerializable(THEME_KEY) as Theme? ?: Theme.LIGHT
        updateTheme()
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        updateAccordingToTheme()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(THEME_KEY, currentTheme)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentTheme = savedInstanceState.getSerializable(THEME_KEY) as Theme
    }

    fun onChangeThemeClick(v: View) {
        currentTheme = when(currentTheme) {
            Theme.LIGHT -> Theme.DARK
            Theme.DARK -> Theme.LIGHT
        }
        recreate()
    }

    private fun updateTheme() {
        when (currentTheme) {
            Theme.LIGHT -> {
                setTheme(R.style.LightTheme)
            }
            Theme.DARK -> {
                setTheme(R.style.DarkTheme)
            }
        }
    }

    private fun updateAccordingToTheme() {
        when (currentTheme) {
            Theme.LIGHT -> {
                themeButton.setImageResource(R.drawable.ic_theme_night)
            }
            Theme.DARK -> {
                themeButton.setImageResource(R.drawable.ic_theme_day)
            }
        }
    }


}