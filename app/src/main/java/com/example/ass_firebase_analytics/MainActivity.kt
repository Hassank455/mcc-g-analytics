package com.example.ass_firebase_analytics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ass_firebase_analytics.adapter.catAdapter
import com.example.ass_firebase_analytics.model.Category
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), catAdapter.onCategoryItemClickListener {

    lateinit var db: FirebaseFirestore
    private var firebaseAnalytics: FirebaseAnalytics? = null

    var st: Long = 0
    var en: Long = 0
    var tot: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        st = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("category screen")

        getCategory()

        add_category.setOnClickListener {
            en = Calendar.getInstance().timeInMillis
            tot = en - st

            val minutes: Long = tot / 1000 / 60
            val seconds = (tot / 1000 % 60)
            timeOfInTheScreen("$minutes m $seconds s","HK4","MainActivity")

            var intent = Intent(this, AddCat::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(data: Category, position: Int) {
        selectContent(data.id,data.nameCategory!!,data.imageCategory!!)
        en = Calendar.getInstance().timeInMillis
        tot = en - st

        val minutes: Long = tot / 1000 / 60
        val seconds = (tot / 1000 % 60)
        timeOfInTheScreen("$minutes m $seconds s","HK4","MainActivity")


        var intent = Intent(this,ProductActivity2::class.java)
        intent.putExtra("id",data.id)
        intent.putExtra("catImage",data.imageCategory)
        intent.putExtra("catName",data.nameCategory)
        startActivity(intent)

    }

    private fun getCategory(){
        val categoryList= mutableListOf<Category>()
        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("HK4", "${document.id} -> ${document.get("category_name")} -> ${document.get("category_image")}")
                        val id = document.id
                        val data = document.data
                        val categoryName = data["category_name"] as String?
                        val categoryImage = data["category_image"] as String?
                        categoryList.add(Category(id, categoryImage, categoryName))
                    }
                    recyclerview_cat?.layoutManager =
                        LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    recyclerview_cat.setHasFixedSize(true)
                    val categoriesAdapter = catAdapter(this, categoryList, this)
                    recyclerview_cat.adapter = categoriesAdapter
                }
            }
    }


    private fun trackScreen(screenName:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun selectContent(id:String, name:String, contentType:String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun timeOfInTheScreen(time: String, userId:String, pageName:String){

        val time= hashMapOf("time" to time,"userId" to userId,"pageName" to pageName)
        db.collection("Time")
            .add(time)
            .addOnSuccessListener {documentReference ->
                Log.e("TAG","time added successfully")
            }
            .addOnFailureListener {exception ->
                Log.e("TAG", exception.message.toString())
            }
    }




}