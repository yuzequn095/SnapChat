package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // initialize local variable
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // get the Text Edit
        emailEditText = findViewById(R.id.Email)
        passwordEditText = findViewById(R.id.Password)

    }

    // check if the currently user signed in
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            logIn()
        }
    }

    // log in user and go to next activity
    fun logIn(){
        // Move to next Activity
        val intent = Intent(this, SnapActivity::class.java)
        startActivity(intent)
    }

    // function for GO button clicked
    fun goClick(view: View){
        // check if we can log in the user
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    /*
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    */
                    // call log in function
                    logIn();

                } else {
                    // If sign in fails, display a message to the user.
                    /*
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                    */
                    // sign up
                    auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                /*
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                updateUI(user)
                                */
                                // add to database
                                // reach the databse
                                val database = FirebaseDatabase.getInstance()
                                val myRef = database.getReference()
                                // reach the further folder
                                myRef.child("users").child(task.result!!.user?.uid!!).child("email").setValue(emailEditText?.text.toString())
                                logIn()
                            } else {
                                 /*
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                                updateUI(null)

                                */
                                Toast.makeText(this, "Login Failed. Try Again.", Toast.LENGTH_SHORT).show()
                            }

                        }
                }

            }

    }
}