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
import android.widget.ArrayAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_pro.*
import java.io.ByteArrayOutputStream

class AddPro : AppCompatActivity() {


    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111

    var imageURI: Uri? = null
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pro)
        db = Firebase.firestore

        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")


        product_cancel.setOnClickListener {
            onBackPressed()
        }

        product_image.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
            Thread.sleep(1000)
            product_image.layoutParams.height = 600
            product_image.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            val param = product_image.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0, -70, 0, 10)
            product_image.layoutParams = param
        }
        getAllCategories()

        btn_save_new_product.setOnClickListener {

            val bitmap = (product_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val data = baos.toByteArray()

            val childRef =
                imageRef.child(System.currentTimeMillis().toString() + "_categoryimages.png")
            var uploadTask = childRef.putBytes(data)
            uploadTask.addOnFailureListener { exception ->
                Log.e("TAG", exception.message.toString())


            }.addOnSuccessListener {

                Log.e("TAG", "Uploaded Successfully")
                childRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.e("TAG", uri.toString())
                    fileURI = uri

                    var product_name = productname.text.toString()
                    var product_price = Productprice.text.toString()
                    var product_description = Productdescription.text.toString()
                    var category_name = productcategory.selectedItem.toString()


                    addProduct(
                        product_name,
                        fileURI.toString(),
                        product_price.toDouble(),
                        product_description,
                        category_name
                    )
                }

            }
        }

    }

    private fun addProduct(
        name: String,
        image: String,
        price: Double,
        description: String?,
        categoryName: String?
    ) {

        val product = hashMapOf(
            "name" to name, "image" to image, "price" to price,
            "description" to description, "categoryName" to categoryName
        )
        db.collection("products")
            .add(product)
            .addOnSuccessListener { documentReference ->
                Log.e("TAG", "added successfully with category id ${documentReference.id}")
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", exception.message.toString())
            }
    }

    private fun getAllCategories() {
        val spinnerList = mutableListOf<String>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("TAG", "${document.id} -> ${document.get("category_name")} ")
                        val data = document.data
                        val categoryName = data["category_name"] as String?
                        spinnerList.add(categoryName!!)

                    }
                    val arrayAdapter = ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1, spinnerList
                    )
                    productcategory.adapter = arrayAdapter
                }
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            Log.e("TAG", imageURI.toString())
            product_image.setImageURI(imageURI)
        }
    }

}