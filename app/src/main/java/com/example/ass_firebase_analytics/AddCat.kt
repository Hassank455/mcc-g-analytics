package com.example.ass_firebase_analytics

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_cat.*
import java.io.ByteArrayOutputStream

class AddCat : AppCompatActivity() {


    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111

    var imageURI: Uri? = null
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_cat)
        db = Firebase.firestore

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")

        category_image.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
            Thread.sleep(2000)
            category_image.getLayoutParams().height = 800
            category_image.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT
            val param = category_image.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, -70, 0, 10)
            category_image.layoutParams = param

        }

        save_new_category.setOnClickListener {

            // Get the data from an ImageView as bytes
            val bitmap = (category_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val childRef =
                imageRef.child(System.currentTimeMillis().toString() + "_categoryimages.png")
            var uploadTask = childRef.putBytes(data)
            uploadTask.addOnFailureListener { exception ->
                Log.e("TAG", exception.message.toString())

                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...

                Log.e("TAG", "Upload Successfully")
                Toast.makeText(this, "Upload Successfully", Toast.LENGTH_SHORT)
                    .show()

                childRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("TAG", uri.toString())
                    fileURI = uri

                    var name = category_name.text.toString()

                    addCategory(name, fileURI.toString())
                }

            }
        }
        category_cancel.setOnClickListener {
            onBackPressed()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            Log.e("TAG", imageURI.toString())
            category_image.setImageURI(imageURI)
        }
    }

    private fun addCategory(name: String, image: String) {
        val category = hashMapOf("category_name" to name, "category_image" to image)
        db.collection("categories")
            .add(category)
            .addOnSuccessListener { documentReference ->
                Log.e(
                    "TAG",
                    "added successfully with category id ${documentReference.id}"
                )
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", exception.message.toString())
            }
    }

}