package com.example.thinkaboutit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.progressindicator.LinearProgressIndicator

class TimerFragment : Fragment() {
    private lateinit var timerText: TextView
    private lateinit var timerProgress: LinearProgressIndicator
    private var totalTime: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateTimerDisplay()
            handler.postDelayed(this, 100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        totalTime = arguments?.getInt("totalTime") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerText = view.findViewById(R.id.timer_text)
        timerProgress = view.findViewById(R.id.timer_progress)
        timerProgress.max = totalTime
        timerProgress.progress = totalTime
        updateTimerDisplay()
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    private fun updateTimerDisplay() {
        val currentTime = GameTimerManager.Instance.timeRemaining
        timerText.text = currentTime.toString()
        timerProgress.progress = currentTime
    }

    companion object {
        fun newInstance(totalTime: Int): TimerFragment {
            val fragment = TimerFragment()
            val args = Bundle()
            args.putInt("totalTime", totalTime)
            fragment.arguments = args
            return fragment
        }
    }
}