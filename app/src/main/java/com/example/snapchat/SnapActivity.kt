package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SnapActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
    }

    // function to load menu/snaps.xml in current activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater // this class is used to instantiate menu XML files into Menu objects
        inflater.inflate(R.menu.snaps, menu) // pass the snap menu we created to menu obj

        return super.onCreateOptionsMenu(menu)
    }

    // function to run when user select some options from menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // check the options selected
        // case: create option
        if(item?.itemId == R.id.createSnap){
            // use intent to load new activity
            val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        }
        // case: logout option
        else if(item?.itemId == R.id.logOut){
            // Initialize Firebase Auth
            auth = Firebase.auth
            // sign out current user from Firebase
            auth.signOut()
            // remove the current activity from stack
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    // function to allow user sign out by clicking back button
    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}