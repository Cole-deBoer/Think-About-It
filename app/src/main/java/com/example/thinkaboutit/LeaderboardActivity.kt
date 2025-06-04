package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LeaderboardActivity : AppCompatActivity(), State {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        enter();

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set up play again button
        val playAgainButton = findViewById<Button>(R.id.play_again_button)
        playAgainButton.setOnClickListener {
            ServiceManager.Instance.setUserReadyness(true)
        }
    }
    // Disable back button
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Do nothing
        Toast.makeText(this, "You can't go back!", Toast.LENGTH_SHORT).show()
    }

    override fun enter() {
        GameManager.Instance.currentState = this
        GameManager.Instance.queuedState = DrawingActivity()

        ServiceManager.Instance.usersRef.get().addOnSuccessListener {snapshot ->
            var winnersId = ""
            var highestVotes = 0;
            for(user in snapshot.children)
            {
                if(user.child("votes").value == null) continue
                if(user.child("votes").value.toString().toInt() >= highestVotes)
                {
                    highestVotes = user.child("votes").value.toString().toInt()
                    winnersId = user.key.toString()
                }
            }

            ServiceManager.Instance.getUserImage(winnersId) {image ->
                // Set up winning drawing
                val winningDrawing = findViewById<ImageView>(R.id.winning_drawing)
                winningDrawing.setImageBitmap(image)
            }
        }
    }

    override fun exit(state: State) {
        startActivity(Intent(this, state::class.java));
        ServiceManager.Instance.setUserReadyness(false)
    }
}
