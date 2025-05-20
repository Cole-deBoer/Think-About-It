package com.example.thinkaboutit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DrawingActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var currPaint: ImageButton
    private lateinit var eraseBtn: ImageButton
    private lateinit var newBtn: ImageButton
    private lateinit var submitBtn: Button
    private lateinit var brushSize: SeekBar
    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

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
    override fun onBackPressed() {
        // Do nothing
    }
}