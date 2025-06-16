package com.example.thinkaboutit

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SessionFullLoadingActivity : AppCompatActivity() {
    private lateinit var stateText: TextView
    private lateinit var gameStateListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_full_loading)

        stateText = findViewById(R.id.session_full_state_text)

        // Use a ValueEventListener to update the state text live
        gameStateListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stateName = snapshot.value?.toString() ?: "Unknown"
                runOnUiThread {
                    stateText.text = "Game in progress: $stateName"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ServiceManager.Instance.episodeRef.child("gameState").addValueEventListener(gameStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceManager.Instance.episodeRef.child("gameState").removeEventListener(gameStateListener)
    }
} 