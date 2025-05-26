package com.example.thinkaboutit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
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

    val usersRef = databaseRef.child("users");
    val promptsRef = databaseRef.child("prompts");

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
            }.addOnSuccessListener {snapshot ->
                if(context != null)
                {
                    Toast.makeText(context, "Image Uploaded", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}