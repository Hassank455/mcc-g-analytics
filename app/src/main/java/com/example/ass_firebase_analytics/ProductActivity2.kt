package com.example.ass_firebase_analytics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ass_firebase_analytics.adapter.proAdapter
import com.example.ass_firebase_analytics.model.Product
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_product2.*
import java.util.*

class ProductActivity2 : AppCompatActivity(), proAdapter.onProductsItemClickListener {

    lateinit var db: FirebaseFirestore
    var catImage: String? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null

    var start: Long = 0
    var end: Long = 0
    var total: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product2)

        start = Calendar.getInstance().timeInMillis

        db = Firebase.firestore
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        trackScreen("product screen")

        catImage = intent.getStringExtra("catImage")
        val catName = intent.getStringExtra("catName")

        txt_category_name.text = catName
        getProductsAccordingToCategory("$catName")

        add_product.setOnClickListener {
            end = Calendar.getInstance().timeInMillis
            total = end - start

            val minutes: Long = total / 1000 / 60
            val seconds = (total / 1000 % 60)
            timeOfInTheScreen("$minutes m $seconds s", "HK4", "ProductActivity")

            var i = Intent(this, AddPro::class.java)
            startActivity(i)
        }

        category_back.setOnClickListener {
            end = Calendar.getInstance().timeInMillis
            total = end - start

            val minutes: Long = total / 1000 / 60
            val seconds = (total / 1000 % 60)
            timeOfInTheScreen("$minutes m $seconds s", "HK4", "ProductActivity")

            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }

    }

    override fun onItemClick(data: Product, position: Int) {
        selectContent(data.id!!, data.name!!, data.image!!)

        end = Calendar.getInstance().timeInMillis
        total = end - start

        val minutes: Long = total / 1000 / 60
        val seconds = (total / 1000 % 60)
        timeOfInTheScreen("$minutes m $seconds s", "HK4", "ProductActivity")

        var i = Intent(this, DetalisActivity3::class.java)
        i.putExtra("pid", data.id)
        i.putExtra("pname", data.name)
        i.putExtra("pimage", data.image)
        i.putExtra("pprice", data.price)
        i.putExtra("pdescription", data.description)
        i.putExtra("pcategory", data.categoryName)
        startActivity(i)

    }

    private fun getProductsAccordingToCategory(catName: String) {
        val dataProduct = mutableListOf<Product>()

        db.collection("products").whereEqualTo("categoryName", catName)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.e("HK4", "${document.id} -> ${document.get("name")}")
                        val id = document.id
                        val data = document.data
                        val name = data["name"] as String?
                        val image = data["image"] as String?
                        val price = data["price"] as Double
                        val description = data["description"] as String?
                        val categoryName = data["categoryName"] as String?
                        dataProduct.add(
                            Product(id, image, name, price, description, categoryName)
                        )
                    }

                    rv_product.layoutManager = GridLayoutManager(this, 2)
                    rv_product.setHasFixedSize(true)
                    val productAdapter = proAdapter(this, dataProduct, this)
                    rv_product.adapter = productAdapter

                }
            }
    }


    private fun trackScreen(screenName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ProductsActivity")
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    private fun selectContent(id: String, name: String, contentType: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun timeOfInTheScreen(time: String, userId: String, pageName: String) {

        val time = hashMapOf("time" to time, "userId" to userId, "pageName" to pageName)
        db.collection("Time")
            .add(time)
            .addOnSuccessListener { documentReference ->
                Log.e("TAG", "adds successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", exception.message.toString())
            }
    }

}