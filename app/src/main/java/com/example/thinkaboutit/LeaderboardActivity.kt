package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.coroutines.runBlocking
import java.util.UUID

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Get the voted image resource ID from intent
        ServiceManager.Instance.auth.currentUser?.let { user ->

            ServiceManager.Instance.getUserImage(user.uid) { image ->
                // Set up winning drawing
                val winningDrawing = findViewById<ImageView>(R.id.winning_drawing)
                winningDrawing.setImageBitmap(image)
            }


            // Set up play again button
            val playAgainButton = findViewById<Button>(R.id.play_again_button)
            playAgainButton.setOnClickListener {
                val intent = Intent(this, DrawingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
    // Disable back button
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing
        Toast.makeText(this, "You can't go back!", Toast.LENGTH_SHORT).show()
    }
}
