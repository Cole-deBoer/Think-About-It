package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.values
import kotlinx.coroutines.flow.first

class VotingActivity : AppCompatActivity(), State {

    lateinit var user1 : VoteCard;
    lateinit var user2 : VoteCard;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)

        enter()

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun vote(uid: String): View.OnClickListener {
        return View.OnClickListener {

            ServiceManager.Instance.usersRef.child(uid).child("votes").setValue(1)
            ServiceManager.Instance.setUserReadyness(true);
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
        GameManager.Instance.queuedState = LeaderboardActivity()

        // if we are the host set the images to be the submitted drawings

        ServiceManager.Instance.hostRef.get().addOnSuccessListener {snapshot ->
            //if(snapshot.value != ServiceManager.Instance.auth.currentUser?.uid) return@addOnSuccessListener

            ServiceManager.Instance.usersRef.get().addOnSuccessListener { usersSnapshot ->
                user1 = VoteCard(findViewById(R.id.drawingImage1), findViewById(R.id.voteButton1), usersSnapshot.children.first().key.toString())
                user2 = VoteCard(findViewById(R.id.drawingImage2), findViewById(R.id.voteButton2), usersSnapshot.children.elementAt(1).key.toString())

                ServiceManager.Instance.getUserImage(user1.userId) { bitmap ->
                    user1.drawing.setImageBitmap(bitmap)
                }
                ServiceManager.Instance.getUserImage(user2.userId) { bitmap ->
                    user2.drawing.setImageBitmap(bitmap)
                }

                user1.voteButton.setOnClickListener(vote(user1.userId))
                user2.voteButton.setOnClickListener(vote(user2.userId))
            }
        }
    }

    override fun exit(state: State) {
        startActivity(Intent(this, state::class.java))
        ServiceManager.Instance.setUserReadyness(false)
    }
}

data class VoteCard(
    val drawing: ImageView,
    val voteButton: Button,
    val userId: String
)
