package com.example.thinkaboutit

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class VotingActivity : AppCompatActivity() {
    private lateinit var drawingImage1: ImageView
    private lateinit var drawingImage2: ImageView
    private lateinit var drawingImage3: ImageView
    private lateinit var voteButton1: Button
    private lateinit var voteButton2: Button
    private lateinit var voteButton3: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Initialize views
        drawingImage1 = findViewById(R.id.drawingImage1)
        drawingImage2 = findViewById(R.id.drawingImage2)
        drawingImage3 = findViewById(R.id.drawingImage3)
        voteButton1 = findViewById(R.id.voteButton1)
        voteButton2 = findViewById(R.id.voteButton2)
        voteButton3 = findViewById(R.id.voteButton3)

        // Set up click listeners to navigate to leaderboard with the voted image
        voteButton1.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("VOTED_IMAGE_RESOURCE", R.drawable.sample_drawing1)
            startActivity(intent)
        }

        voteButton2.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("VOTED_IMAGE_RESOURCE", R.drawable.sample_drawing2)
            startActivity(intent)
        }

        voteButton3.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("VOTED_IMAGE_RESOURCE", R.drawable.sample_drawing3)
            startActivity(intent)
        }

        // Set up sample drawings
        drawingImage1.setImageResource(R.drawable.sample_drawing1)
        drawingImage2.setImageResource(R.drawable.sample_drawing2)
        drawingImage3.setImageResource(R.drawable.sample_drawing3)
    }

    // Disable back button
    override fun onBackPressed() {
        // Do nothing
    }
}
