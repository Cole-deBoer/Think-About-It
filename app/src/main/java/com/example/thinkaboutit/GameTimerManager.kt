package com.example.thinkaboutit

import android.os.CountDownTimer
import android.util.Log

class GameTimerManager {
    companion object
    {
        val Instance by lazy { GameTimerManager() };
    }

    private var timer: CountDownTimer? = null
    var timeRemaining : Int = 0;

    fun startTimer(timeToWait: Long, callback: () -> Unit) {
        // ensure no duplicate timers. Cancel any existing ones
        cancelTimer()

        val timeToSeconds = timeToWait * 1000
        timer = object : CountDownTimer(timeToSeconds, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = (millisUntilFinished / 1000).toInt()
                Log.d("GameTimer", "Time remaining: $timeRemaining sec")
            }

            override fun onFinish() {
                callback()
            }
        }.start()
    }

    fun cancelTimer() {
        timer?.cancel()
        timer = null
        timeRemaining = 0
    }
}