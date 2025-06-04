package com.example.thinkaboutit

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubmitActivity : AppCompatActivity(), State {

    private lateinit var drawingPreview: ImageView
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button
    private lateinit var titleText: TextView
    private var drawingPath: String? = null
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit)

        enter();

        // Disable going back
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Initialize views
        drawingPreview = findViewById(R.id.drawing_preview)
        submitButton = findViewById(R.id.confirm_submit_btn)
        cancelButton = findViewById(R.id.cancel_btn)
        titleText = findViewById(R.id.title_text)

        // Get drawing path and user name from intent
        drawingPath = intent.getStringExtra("DRAWING_PATH")
        userName = intent.getStringExtra("USER_NAME") ?: "Artist"

        // Set title with user name
        titleText.text = "$userName's Drawing"

        // Display the drawing
        drawingPath?.let {
            drawingPreview.setImageBitmap(BitmapFactory.decodeFile(it))
        }

        // Set up submit button
        submitButton.setOnClickListener {

            ServiceManager.Instance.setUserReadyness(true);
        }

        // Set up cancel button
        cancelButton.setOnClickListener {
            finish()
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
    }

    override fun exit(state: State) {
        startActivity(Intent(this, state::class.java));
        ServiceManager.Instance.setUserReadyness(false)
    }
}