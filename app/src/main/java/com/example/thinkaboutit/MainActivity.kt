package com.example.thinkaboutit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var letsGoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Initialize views
        nameEditText = findViewById(R.id.name_edit_text)
        letsGoButton = findViewById(R.id.lets_go_button)

        // Set up button click listener
        letsGoButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                // Start drawing activity with the entered name and clear the stack
                val intent = Intent(this, DrawingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("USER_NAME", name)
                startActivity(intent)
            }
        }
    }

    // Disable back button
    override fun onBackPressed() {
        // Do nothing
    }
}