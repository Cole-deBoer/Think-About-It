package com.example.thinkaboutit

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class UserReadynessTracker : ValueEventListener {

    private lateinit var snapshot : DataSnapshot

    override fun onDataChange(snapshot: DataSnapshot) {
        this.snapshot = snapshot
        ServiceManager.Instance.getUserCount { count ->
            if (count != GameManager.Instance.amountOfPlayers) return@getUserCount

            ServiceManager.Instance.checkIsHost { isHost ->
                if (!isHost) return@checkIsHost

                areReady { isReady ->
                    if (!isReady) return@areReady
                    GameManager.Instance.queuedState?.let { state ->
                        ServiceManager.Instance.setGameState(state)
                    }
                }
            }
        }
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

    private fun areReady(callback : (isReady: Boolean)-> Unit) {
        var allUsersReady = true;
        for(user in snapshot.children)
        {
            if(user != null && user.child("name").value != null)
            {
                if(!user.child("ready").value.toString().toBoolean())
                {
                    allUsersReady = false;
                    break;
                }
            }
        }
        callback(allUsersReady)
    }
}