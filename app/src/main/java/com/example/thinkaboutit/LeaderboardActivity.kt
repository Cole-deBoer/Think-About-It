package com.example.thinkaboutit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Get the voted image resource ID from intent
        val votedImageResource = intent.getIntExtra("VOTED_IMAGE_RESOURCE", R.drawable.sample_drawing1)

        // Set up winning drawing
        val winningDrawing = findViewById<ImageView>(R.id.winning_drawing)
        winningDrawing.setImageResource(votedImageResource)

        // Set up play again button
        val playAgainButton = findViewById<Button>(R.id.play_again_button)
        playAgainButton.setOnClickListener {
            val intent = Intent(this, DrawingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // Disable back button
    override fun onBackPressed() {
        // Do nothing
    }
}
