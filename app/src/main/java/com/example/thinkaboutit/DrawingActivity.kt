package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DrawingActivity : AppCompatActivity(), State {

    private lateinit var drawingView: DrawingView
    private lateinit var currPaint: ImageButton
    private lateinit var eraseBtn: ImageButton
    private lateinit var newBtn: ImageButton
    private lateinit var submitBtn: Button
    private lateinit var brushSize: SeekBar
    private lateinit var promptText: TextView
    private var userName: String = ""
    private lateinit var promptListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        enter()

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Get user name from intent
        userName = intent.getStringExtra("USER_NAME") ?: "Artist"

        // Initialize views
        drawingView = findViewById(R.id.drawing_view)
        currPaint = findViewById(R.id.color_black)
        eraseBtn = findViewById(R.id.erase_btn)
        newBtn = findViewById(R.id.new_btn)
        submitBtn = findViewById(R.id.submit_btn)
        brushSize = findViewById(R.id.brush_size)
        promptText = findViewById(R.id.prompt_text)

        // Set current paint to black by default
        currPaint.setImageDrawable(resources.getDrawable(R.drawable.selected_color, theme))

        // Set up brush size slider
        brushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                drawingView.setBrushSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Not needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Not needed
            }
        })

        // Set up erase button
        eraseBtn.setOnClickListener {
            drawingView.setErase(true)
        }

        // Set up new button
        newBtn.setOnClickListener {
            drawingView.startNew()
        }

        // Set up submit button
        submitBtn.setOnClickListener {
            // Save the drawing to a file
            val savedImagePath = drawingView.saveDrawing(this)

            // Create intent to pass the drawing to another activity and clear stack
            val submitIntent = Intent(this, SubmitActivity::class.java)
            submitIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            submitIntent.putExtra("DRAWING_PATH", savedImagePath)
            submitIntent.putExtra("USER_NAME", userName)
            startActivity(submitIntent)
        }

        // Add timer fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.timer_container, TimerFragment.newInstance(timeLimit.toInt()))
            .commit()
    }

    // Method to handle color selection
    fun paintClicked(view: View) {
        // Use color that was clicked
        if (view !== currPaint) {
            // Update color
            val imgView = view as ImageButton
            val color = view.tag.toString()
            drawingView.setColor(color)

            // Update UI
            imgView.setImageDrawable(resources.getDrawable(R.drawable.selected_color, theme))
            currPaint.setImageDrawable(resources.getDrawable(R.drawable.color_palette, theme))
            currPaint = imgView

            // Disable erase mode
            drawingView.setErase(false)
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
        GameManager.Instance.queuedState = VotingActivity()

        // Set up prompt listener
        promptListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val prompt = snapshot.value?.toString() ?: "Loading prompt..."
                promptText.text = prompt
            }

            override fun onCancelled(error: DatabaseError) {
                promptText.text = "Error loading prompt"
            }
        }
        ServiceManager.Instance.episodeRef.child("prompt").addValueEventListener(promptListener)

        GameTimerManager.Instance.startTimer(timeLimit) {
            Toast.makeText(this, "Time's up!", Toast.LENGTH_SHORT).show()
            ServiceManager.Instance.setGameState(GameManager.Instance.queuedState as State)
        }
    }

    override fun exit(state: State) {
        // Remove the prompt listener
        ServiceManager.Instance.episodeRef.child("prompt").removeEventListener(promptListener)
        startActivity(Intent(this, state::class.java))
        ServiceManager.Instance.setUserReadyness(false)
    }

    override val timeLimit: Long get() = 60
}