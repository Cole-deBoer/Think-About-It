package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity(), State {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        enter()

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    // Disable back button
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing
        Toast.makeText(this, "You can't go back!", Toast.LENGTH_SHORT).show()
    }

    override fun enter() {
        GameManager.Instance.currentState = this
    }

    override fun exit(state: State) {
        startActivity(Intent(this, state::class.java))
        ServiceManager.Instance.setUserReadyness(false)
    }

    override val timeLimit: Long get() = 0
} 