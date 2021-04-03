package com.example.ass_firebase_analytics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ass_firebase_analytics.R
import com.example.ass_firebase_analytics.model.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pro_item.view.*

class proAdapter(
    var activity: Context?,
    var data: MutableList<Product>,
    var clickListener: proAdapter.onProductsItemClickListener
) : RecyclerView.Adapter<proAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val productImage  =itemView.product_photo
        val name=itemView.product_name
        val price=itemView.product_price

        fun initialize(data: Product, action:onProductsItemClickListener){

            Picasso.get().load(data.image).into(productImage)
            name.text = data.name
            price.text = data.price.toString()

            itemView.setOnClickListener {
                action.onItemClick(data,adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): proAdapter.MyViewHolder {
        var View: View = LayoutInflater.from(activity).inflate(R.layout.pro_item,parent,false)
        val myHolder:MyViewHolder = MyViewHolder(View)
        return myHolder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: proAdapter.MyViewHolder, position: Int) {
        holder.initialize(data.get(position), clickListener)
    }
    interface onProductsItemClickListener{
        fun onItemClick(data:Product, position: Int)
    }
}