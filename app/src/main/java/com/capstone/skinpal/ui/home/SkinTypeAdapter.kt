package com.capstone.skinpal.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinpal.R

class SkinTypeAdapter(
    private val images: List<Int>,
    private val titles: List<String>,
    private val descriptions: List<String>
) : RecyclerView.Adapter<SkinTypeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleView: TextView = itemView.findViewById(R.id.slideTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.slideDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skin_type_slide, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        holder.titleView.text = titles[position]
        holder.descriptionView.text = descriptions[position]
    }

    override fun getItemCount(): Int = images.size
}
