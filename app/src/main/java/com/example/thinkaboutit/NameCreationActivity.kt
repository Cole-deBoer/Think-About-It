package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

class NameCreationActivity : AppCompatActivity(), State {

    private lateinit var nameEditText: EditText
    private lateinit var letsGoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enter()

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
                ServiceManager.Instance.auth.signInAnonymously().addOnSuccessListener(ServiceManager.Instance.createNewUser(name))
                startActivity(Intent(this, LoadingActivity::class.java))
            }
        }
    }

    // Disable back button
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing
        Toast.makeText(this, "You can't go back!", Toast.LENGTH_SHORT).show()
    }

    override fun enter() {
        ServiceManager.Instance.initializeComponents {
            GameManager.Instance.currentState = this
            GameManager.Instance.queuedState = DrawingActivity()
        }
    }

    override fun exit(state: State) {
        startActivity(Intent(this, state::class.java));
        ServiceManager.Instance.setUserReadyness(false)
    }

    override val timeLimit: Long get() = 3
}
