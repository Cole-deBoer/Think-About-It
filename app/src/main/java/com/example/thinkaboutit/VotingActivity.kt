package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VotingActivity : AppCompatActivity(), State {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)

        enter()

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun vote(uid: String): View.OnClickListener {
        return View.OnClickListener {
            // check that we arent ready when we vote, this ensures we don't vote twice
            ServiceManager.Instance.usersRef.child(ServiceManager.Instance.auth.currentUser?.uid.toString()).get().addOnSuccessListener { userSnapshot ->
                if(userSnapshot.child("ready").value.toString().toBoolean()) return@addOnSuccessListener
                ServiceManager.Instance.usersRef.child(uid).child("votes").get().addOnSuccessListener { snapshot ->
                    if(snapshot.value == null)
                    {
                        snapshot.ref.setValue(1)
                        return@addOnSuccessListener
                    }
                    val currentValue = snapshot.value.toString().toInt()
                   snapshot.ref.setValue(currentValue + 1)
                }
                ServiceManager.Instance.setUserReadyness(true);
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
        GameManager.Instance.currentState = this
        GameManager.Instance.queuedState = LeaderboardActivity()

        // Add timer fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.timer_container, TimerFragment.newInstance(timeLimit.toInt()))
            .commit()

        ServiceManager.Instance.usersRef.get().addOnSuccessListener { usersSnapshot ->

            for(user in usersSnapshot.children)
            {
                //if(user.key == ServiceManager.Instance.auth.currentUser?.uid) continue

                val card = LayoutInflater.from(this).inflate(R.layout.user_vote_card, findViewById(R.id.verticalLayout_id), false)


                ServiceManager.Instance.getUserImage(user.key.toString()) { bitmap ->
                    val userCard = VoteCard(card.findViewById(R.id.image_id), card.findViewById(R.id.voteButton_id), user.key.toString())

                    userCard.drawing.setImageBitmap(bitmap)
                    userCard.voteButton.setOnClickListener(vote(userCard.userId))
                    findViewById<LinearLayout>(R.id.verticalLayout_id).addView(card)
                }
            }
        }
    }

    override fun exit(state: State) {
        startActivity(Intent(this, state::class.java))
        ServiceManager.Instance.setUserReadyness(false)

        GameTimerManager.Instance.startTimer(timeLimit) {
            Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show()
            ServiceManager.Instance.setGameState(GameManager.Instance.queuedState as State)
        }
    }

    override val timeLimit: Long get() = 10
}

data class VoteCard(
    val drawing: ImageView,
    val voteButton: Button,
    val userId: String
)
