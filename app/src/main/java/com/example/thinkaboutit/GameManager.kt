package com.example.thinkaboutit

// singleton class used to keep track of the game state and to pass relevant data
// between activities
class GameManager()
{
    companion object {
        val Instance = GameManager;
    }
}

