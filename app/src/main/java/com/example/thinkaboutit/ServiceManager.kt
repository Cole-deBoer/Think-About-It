package com.example.thinkaboutit

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

// Singleton class that Manages all the firebase integration throughout the app
class ServiceManager private constructor()
{
    companion object {
        val Instance by lazy { ServiceManager() };
    }

    private val app = Firebase.app;
    val auth = Firebase.auth;
    val database = Firebase.database(app);
    val storage = Firebase.storage(app)

    private val databaseRef = database.reference;
    private val storageRef = storage.reference;

    private lateinit var episodeRef : DatabaseReference;

    lateinit var usersRef: DatabaseReference;
    lateinit var hostRef: DatabaseReference;
    lateinit var promptsRef: DatabaseReference;

    lateinit var GameStateListener: GameStateListener;
    private lateinit var userReadynessTrackerListener: UserReadynessTracker;

    fun initializeComponents(callback: () -> Unit) {
        databaseRef.child("currentEpisode").get().addOnSuccessListener {episode ->
            episodeRef = databaseRef.child("episode_${episode.value}");
            usersRef = episodeRef.child("users");
            hostRef = episodeRef.child("host");
            promptsRef = episodeRef.child("Prompts");

            userReadynessTrackerListener = UserReadynessTracker();
            usersRef.addValueEventListener(userReadynessTrackerListener);

            GameStateListener = GameStateListener();
            episodeRef.child("gameState").addValueEventListener(GameStateListener);

            callback()
        }.addOnFailureListener {
            Log.e("Connection Error", "Couldn't get the current episode")
        }
    }

    // method takes a callback
    fun getUserImage(id: String, callback: (Bitmap?) -> Unit)
    {
        Log.d("IMAGE ID", id)
        val imagePath = storageRef.child("${id}.png")
        val oneMegabyte: Long = 1024 * 1024
        imagePath.getBytes(oneMegabyte).addOnSuccessListener {bytes ->
            if(bytes != null)
            {
                // pass the bitmap to the callback
                callback(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
            }
            else
            {
                Log.e("IMAGE NULL ERROR", "Check your file path and ensure the image is on firebase")
            }
        }.addOnFailureListener {
            Log.e("RETRIEVING IMAGE ERROR", "Couldn't retrieve image from firebase")
            callback(null)
        }
    }

    fun sendUserImage(image: Bitmap, context: Context?) {
        auth.currentUser?.let {user ->
            val imageReference = storageRef.child("${user.uid}.png")

            val byteOutputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream)
            val data = byteOutputStream.toByteArray()

            imageReference.putBytes(data).addOnFailureListener {
                if(context != null)
                {
                    Toast.makeText(context, "Image Upload Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getGameState(callback: (State) -> Unit)
    {
        episodeRef.child("gameState").get().addOnFailureListener {
            Log.e("Connection Error", "Couldn't get the game state")
        }.addOnSuccessListener { snapshot ->
            if(snapshot.value.toString().isEmpty())
            {
                Log.e("State Error", "Couldn't find state object in snapshot")
            }
            else
            {
                snapshot.value.let { stateName ->
                    val state = StateTable.states.get(key = stateName.toString());
                    if(state != null)
                    {
                        callback(state)
                    }
                    else
                    {
                        Log.e("State Error", "Couldn't find state $stateName in state table. loading name creating activity instead")
                        callback(NameCreationActivity())
                    }
                }
            }
        }
    }

    fun setGameState(state: State) {
        episodeRef.child("host").get().addOnFailureListener {
            Log.e("Connection Error", "Couldn't get the host")
        }.addOnSuccessListener { snapshot ->
            if(auth.currentUser?.uid == snapshot.value.toString())
            {
                episodeRef.child("gameState").get().addOnFailureListener {
                    Log.e("Connection Error", "Couldn't get the game state creating a new one")
                    episodeRef.child("gameState").setValue(state.javaClass.simpleName)
                }.addOnSuccessListener { snapshot ->
                    snapshot.ref.setValue(state.javaClass.simpleName)
                }
            }
        }
    }

    fun setUserReadyness(value : Boolean) {
        usersRef.child(auth.currentUser?.uid.toString()).get().addOnFailureListener {
            Log.e("Connection Error", "Couldn't get the user id")
        }.addOnSuccessListener { snapshot ->
            snapshot.ref.child("ready").setValue(value)
            if (value) {
                // Show loading screen when user is ready
                val currentActivity = GameManager.Instance.currentState
                if (currentActivity !is LoadingActivity) {
                    val intent = Intent(currentActivity as? AppCompatActivity, LoadingActivity::class.java)
                    currentActivity?.let { activity ->
                        (activity as? AppCompatActivity)?.startActivity(intent)

                    }
                }
            }
        }
    }

    fun checkIsHost(callback: (Boolean) -> Unit) {
        episodeRef.child("host").get().addOnFailureListener {
            Log.e("Connection Error", "Couldn't get the host")
        }.addOnSuccessListener {
            callback(it.value.toString() == auth.currentUser?.uid)
        }
    }


    fun getUserCount (callback: (Int) -> Unit) {
        usersRef.get().addOnSuccessListener { snapshot ->
            callback(snapshot.childrenCount.toInt())
        }
    }

    fun createNewUser(name: String): OnSuccessListener<in AuthResult> {
        return OnSuccessListener {
            episodeRef.child("host").get().addOnFailureListener {
                Log.e("Connection Error", "Couldn't get the host")
            }.addOnSuccessListener { snapshot ->
                if (snapshot.value == null) {
                    snapshot.ref.setValue(auth.currentUser?.uid.toString())
                }
                usersRef.child(auth.currentUser?.uid.toString()).child("name").setValue(name)
                usersRef.child(auth.currentUser?.uid.toString()).child("ready").setValue(true)
            }
        }
    }

    fun setPrompt() {
        checkIsHost {
            if(!it) return@checkIsHost
            promptsRef.get().addOnSuccessListener { snapshot ->
                // get random prompt in the list of prompts
                val promptIndex = (0..snapshot.childrenCount).random()
                episodeRef.child("prompt").setValue(snapshot.child(promptIndex.toString()).value.toString())
            }
        }
    }

    fun getPrompt(callback: (String) -> Unit) {
        episodeRef.child("prompt").get().addOnSuccessListener {
            callback(it.value.toString())
        }
    }

    fun clearEpisode() {
        checkIsHost {
            if (!it) return@checkIsHost

            episodeRef.removeValue()
            usersRef.removeValue()
            hostRef.removeValue()
            promptsRef.removeValue()
            setGameState(NameCreationActivity())
        }
    }
}
