package com.example.ass_firebase_analytics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ass_firebase_analytics.R
import com.example.ass_firebase_analytics.model.Category
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cat_item.view.*


class catAdapter(
    var activity: Context?,
    var data: MutableList<Category>,
    var clickListener: onCategoryItemClickListener
) : RecyclerView.Adapter<catAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageCategory  =itemView.catImg
        val nameCategory=itemView.categoryName

        fun initialize(data: Category, action:onCategoryItemClickListener){
            // imageCategory.setImageURI(Uri.parse(data.imageCategory))
            Picasso.get().load(data.imageCategory).into(imageCategory)

            nameCategory.text = data.nameCategory

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): catAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.cat_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.initialize(data.get(position), clickListener)
    }
    interface onCategoryItemClickListener{
        fun onItemClick(data:Category, position: Int)
    }
}
