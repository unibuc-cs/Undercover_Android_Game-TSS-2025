package com.example.undercover.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.undercover.R

class TestXmlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val playerCountEditText = findViewById<EditText>(R.id.playerCountEditText)
        val startGameButton = findViewById<Button>(R.id.startGameButton)
        val errorMessageTextView = findViewById<TextView>(R.id.errorMessageTextView)

        startGameButton.setOnClickListener {
            val playerCountText = playerCountEditText.text.toString()
            val playerCount = playerCountText.toIntOrNull()

            if (playerCount == null || playerCount < 3 || playerCount > 20) {
                errorMessageTextView.visibility = TextView.VISIBLE
            } else {
                errorMessageTextView.visibility = TextView.GONE
            }
        }
    }
}
