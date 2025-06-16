package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class LoadingActivity : AppCompatActivity(), State {
    private lateinit var readyCountText: TextView
    private lateinit var readyCountListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        readyCountText = findViewById(R.id.ready_count_text)
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
        
        // Set up listener for ready count
        readyCountListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var readyCount = 0
                for (user in snapshot.children) {
                    if (user.child("ready").value.toString().toBoolean()) {
                        readyCount++
                    }
                }
                readyCountText.text = "$readyCount/${GameManager.Instance.amountOfPlayers} players ready"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        ServiceManager.Instance.usersRef.addValueEventListener(readyCountListener)
    }

    override fun exit(state: State) {
        // Remove the listener when exiting
        ServiceManager.Instance.usersRef.removeEventListener(readyCountListener)
        startActivity(Intent(this, state::class.java))
        ServiceManager.Instance.setUserReadyness(false)
    }

    override val timeLimit: Long get() = 0
} 