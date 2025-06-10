package com.example.thinkaboutit

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

// singleton class used to keep track of the game state and to pass relevant data
// between activities
class GameManager
{
    companion object {
        val Instance by lazy { GameManager() }
    }

    val amountOfPlayers: Int = 2
    var currentState: State? = null
    var queuedState: State? = null
}

class GameStateListener : ValueEventListener
{
    override fun onDataChange(snapshot: DataSnapshot) {
        if(snapshot.value != null)
        {
            syncToServerGameState()
        }
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

    private fun syncToServerGameState() {
        ServiceManager.Instance.getGameState { state ->
            GameManager.Instance.currentState?.exit(state)
        }
    }
}

