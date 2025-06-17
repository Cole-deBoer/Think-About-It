package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.Dialog
import android.view.Window
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import java.io.Serializable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LeaderboardActivity : AppCompatActivity(), State, OnUserImageClick {

    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val users = mutableListOf<User>()
    private var winnerImage: Bitmap? = null
    private lateinit var promptListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val winnerText = findViewById<TextView>(R.id.winner_text)
        val winnerImageView = findViewById<ImageView>(R.id.winning_drawing)
        val promptText = findViewById<TextView>(R.id.prompt_text)

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.leaderboard_recycler_view)
        leaderboardAdapter = LeaderboardAdapter(users, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = leaderboardAdapter

        enter()

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Set up play again button
        val playAgainButton = findViewById<Button>(R.id.play_again_button)
        playAgainButton.setOnClickListener {
            ServiceManager.Instance.setUserReadyness(true)
        }

        // Expand winner image on click
        winnerImageView.setOnClickListener {
            winnerImage?.let { bmp -> showImageDialog(bmp) }
        }
    }

    override fun onUserImageClick(bitmap: Bitmap) {
        showImageDialog(bitmap)
    }

    private fun showImageDialog(bitmap: Bitmap) {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_image_expanded, findViewById(R.id.dialog_container))
        val imageView = view.findViewById<ImageView>(R.id.expanded_image)
        val closeButton = view.findViewById<ImageButton>(R.id.close_button)
        val container = view.findViewById<LinearLayout>(R.id.dialog_container)

        imageView.setImageBitmap(bitmap)
        closeButton.setOnClickListener { dialog.dismiss() }

        // Animate the dialog container
        container.scaleX = 0.8f
        container.scaleY = 0.8f
        container.alpha = 0f
        container.animate()
            .scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(250)
            .start()

        dialog.setContentView(view)
        dialog.show()
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

        // Add timer fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.timer_container, TimerFragment.newInstance(timeLimit.toInt()))
            .commit()

        // Listen for prompt updates from Firebase
        val promptText = findViewById<TextView>(R.id.prompt_text)
        promptListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val prompt = snapshot.value?.toString() ?: "Prompt"
                promptText.text = prompt
            }
            override fun onCancelled(error: DatabaseError) {
                promptText.text = "Error loading prompt"
            }
        }
        ServiceManager.Instance.episodeRef.child("prompt").addValueEventListener(promptListener)

        GameTimerManager.Instance.startTimer(timeLimit) {
            Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show()
            ServiceManager.Instance.checkIsHost { isHost ->
                if (isHost) {
                    ServiceManager.Instance.clearEpisode()
                }
                val intent = Intent(this, SessionFullLoadingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        val winnerText = findViewById<TextView>(R.id.winner_text)
        val winnerImageView = findViewById<ImageView>(R.id.winning_drawing)

        ServiceManager.Instance.usersRef.get().addOnSuccessListener { snapshot ->
            val votes: ArrayList<User> = arrayListOf()
            for (user in snapshot.children) {
                var userVotes = 0
                var name = ""
                if (user.child("votes").value != null) {
                    userVotes = user.child("votes").value.toString().toInt()
                }
                if (user.child("name").value != null) {
                    name = user.child("name").value.toString()
                }
                votes.add(User(user.key.toString(), name, userVotes))
            }
            votes.sortByDescending { it.votes }

            // Fetch images for all users asynchronously
            users.clear()
            val total = votes.size
            var loaded = 0
            for ((index, user) in votes.withIndex()) {
                ServiceManager.Instance.getUserImage(user.uid) { image ->
                    users.add(user.copy(image = image))
                    loaded++
                    if (loaded == total) {
                        users.sortByDescending { it.votes }
                        leaderboardAdapter.notifyItemInserted(index)

                        // Set winner's drawing and text
                        if (users.isNotEmpty()) {
                            winnerText.text = "The Winner is ${users[0].name}!";
                            winnerImage = users[0].image
                            winnerImageView.setImageBitmap(users[0].image)
                        }
                    }
                }
            }
        }
    }


    override fun exit(state: State) {
        // Remove the prompt listener when exiting
        ServiceManager.Instance.episodeRef.child("prompt").removeEventListener(promptListener)
        GameTimerManager.Instance.cancelTimer()
        startActivity(Intent(this, state::class.java));
        ServiceManager.Instance.setUserReadyness(false)
    }

    override val timeLimit: Long get() = 15
}

data class User (val uid: String, val name: String, val votes: Int, val image: Bitmap? = null)
