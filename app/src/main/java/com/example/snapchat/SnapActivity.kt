package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SnapActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        snapsListView = findViewById(R.id.snapsListView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        auth = Firebase.auth
        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser.uid).child("snaps").addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                emails.add(snapshot?.child("from")?.value as String)
                snaps.add(snapshot!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {
                var idx = 0
                for(snap: DataSnapshot in snaps){
                    if(snap.key == snapshot?.key){
                        snaps.removeAt(idx)
                        emails.removeAt(idx)
                    }
                    idx++
                }
                adapter.notifyDataSetChanged()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        snapsListView?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot = snaps.get(position)

            /* reference on how to open new intent
            val taskSnapshot = uploadTask.snapshot.uploadSessionUri
            val intent: Intent = Intent(this, ChooseUserActivity::class.java)
            intent.putExtra("imageURL", taskSnapshot.toString())
            intent.putExtra("imageName", imageName)
            intent.putExtra("message", messageEditText?.text.toString())
            startActivity(intent)
            */
            /*
            val intent = Intent(this, ViewSnapActivity::class.java)
            // val intent: Intent = Intent(this, ViewSnapActivity::class.java)

            // all elements in snap
            intent.putExtra("imageName", snapshot.child("imageName").value as String)
            intent.putExtra("imageURL", snapshot.child("imageURL").toString())
            intent.putExtra("message", snapshot.child("message").value as String)
            intent.putExtra("snapKey", snapshot.key)

            startActivity(intent)
            */
            var viewSnapchatsIntent =  Intent(this, ViewSnapActivity::class.java)

            viewSnapchatsIntent.putExtra("imageName", snapshot.child("imageName").value as String)
            viewSnapchatsIntent.putExtra("imageURL", snapshot.child("imageURL").value as String)
            viewSnapchatsIntent.putExtra("message", snapshot.child("message").value as String)
            viewSnapchatsIntent.putExtra("snapkey", snapshot.key)

            startActivity(viewSnapchatsIntent)
        }
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