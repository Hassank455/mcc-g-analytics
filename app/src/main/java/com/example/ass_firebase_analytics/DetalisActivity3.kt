package com.example.ass_firebase_analytics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detalis3.*
import java.util.*

class DetalisActivity3 : AppCompatActivity() {

    lateinit var db: FirebaseFirestore
    private var firebaseAnalytics: FirebaseAnalytics? = null

    var start: Long = 0
    var end: Long = 0
    var total: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalis3)


        start = Calendar.getInstance().timeInMillis

        db= Firebase.firestore
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("product details screen")

        val nName = intent.getStringExtra("pname")
        val nImage = intent.getStringExtra("pimage")
        val nPrice = intent.getDoubleExtra("pprice",0.0)
        val nDescription = intent.getStringExtra("pdescription")

        Picasso.get().load(nImage).into(product_image)
        product_name.text = nName
        product_price.text = nPrice.toString()
        product_desc.text = nDescription


        back_to_products.setOnClickListener {
            end = Calendar.getInstance().timeInMillis
            total = end - start

            val minutes: Long = total / 1000 / 60
            val seconds = (total / 1000 % 60)
            timeOfInTheScreen("$minutes m $seconds s","HK4","ProductDetails")

            onBackPressed()
        }

    }

    private fun trackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ProductDetails")
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun timeOfInTheScreen(time: String, userId:String, pageName:String){

        val time= hashMapOf("time" to time,"userId" to userId,"pageName" to pageName)
        db.collection("Time")
            .add(time)
            .addOnSuccessListener {documentReference ->
                Log.e("TAG","added successfully")
            }
            .addOnFailureListener {exception ->
                Log.e("TAG", exception.message.toString())
            }
    }

}