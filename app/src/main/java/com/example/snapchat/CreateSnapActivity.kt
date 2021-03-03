package com.example.snapchat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

class CreateSnapActivity : AppCompatActivity() {

    // initialize some variables
    var createSnapImageView: ImageView? = null
    var messageEditText: EditText? = null
    val imageName = UUID.randomUUID().toString() + ".jpg"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        createSnapImageView = findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.messageEditText)

    }


    // ask device permission to choose image
    fun chooseImageClicked(view: View){
        // If we don't got the permission to choose photos from outside
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1) // request the permission
        }
        else{
            getPhoto()
        }
    }


    // Callback for the result from requesting permissions.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getPhoto()
            }
        }
    }

    // select photos from device
    fun getPhoto(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }


    // get the result from the previous activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data // get the return image from previous activity

        // if return data is valid
        if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            }
            catch(e: Exception){
                e.printStackTrace()
            }
        }
    }


    // when next button clicked
    /*
    fun next (view: View) {
        createSnapImageView?.setDrawingCacheEnabled(true)
        createSnapImageView?.buildDrawingCache()
        val bitmap = (createSnapImageView?.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Issue", Toast.LENGTH_LONG).show()

        }.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot->
            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

        })
    }
    */

    fun nextClicked(view: View){
        // Get the data from an ImageView as bytes
        createSnapImageView?.isDrawingCacheEnabled = true
        createSnapImageView?.buildDrawingCache()
        // val bitmap = (createSnapImageView.drawable as BitmapDrawable).bitmap
        val bitmap = createSnapImageView?.getDrawingCache()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // get specified image to image
        // and upload image
        var uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener{ taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...

            /*
            var url: String = ""
            var task = it.storage.downloadUrl
            task.addOnSuccessListener {
                url = task.getResult().toString()
                Log.i("URL   ",url)
            */

            val downloadUrl = taskSnapshot.uploadSessionUri
            Log.i("URL", downloadUrl.toString())

            // open choose user inteng
            val intent = Intent(this, ChooseUserActivity::class.java)
            startActivity(intent)
        }
    }




}