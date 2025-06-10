package com.example.thinkaboutit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Drawing path
    private val drawPath = Path()
    // Drawing and canvas paint
    private val drawPaint = Paint()
    private val canvasPaint = Paint(Paint.DITHER_FLAG)
    // Initial color
    private var paintColor = Color.BLACK
    // Canvas
    private lateinit var drawCanvas: Canvas
    // Canvas bitmap
    private lateinit var canvasBitmap: Bitmap
    // Brush size
    private var brushSize = 10f
    // Erase flag
    private var erase = false

    init {
        setupDrawing()
    }

    // Setup drawing
    private fun setupDrawing() {
        // Prepare for drawing and setup paint stroke properties
        drawPaint.color = paintColor
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = brushSize
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    // Size assigned to view
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
    }

    // Draw the view - will be called after touch event
    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath, drawPaint)
    }

    // Register user touches as drawing action
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> drawPath.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawCanvas.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
            else -> return false
        }
        // Redraw
        invalidate()
        return true
    }

    // Update color
    fun setColor(newColor: String) {
        invalidate()
        paintColor = Color.parseColor(newColor)
        drawPaint.color = paintColor
    }

    // Set brush size
    fun setBrushSize(newSize: Float) {
        brushSize = newSize
        drawPaint.strokeWidth = brushSize
    }

    // Set erase mode
    fun setErase(isErase: Boolean) {
        erase = isErase
        if (erase) {
            drawPaint.color = Color.WHITE
        } else {
            drawPaint.color = paintColor
        }
    }

    // Start new drawing
    fun startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    // Save the drawing to a file
    fun saveDrawing(context: Context): String? {
        try {
            // Create a file to save the drawing
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "Drawing_$timeStamp.png"

            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File(storageDir, fileName)
            ServiceManager.Instance.sendUserImage(canvasBitmap, context)

            val fos = FileOutputStream(imageFile)
            canvasBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            return imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving drawing", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}