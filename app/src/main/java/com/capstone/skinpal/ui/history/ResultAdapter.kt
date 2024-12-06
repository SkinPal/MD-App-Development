package com.capstone.skinpal.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.skinpal.data.remote.response.MoisturizerItem
import com.capstone.skinpal.R
import androidx.recyclerview.widget.DiffUtil

// Add DiffUtil.ItemCallback for optimal list updates
class ResultAdapter : ListAdapter<MoisturizerItem, ResultAdapter.ResultAdapterViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ResultAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultAdapterViewHolder, position: Int) {
        val product = getItem(position) // Use getItem(position) for ListAdapter
        holder.bind(product)
    }

    override fun getItemCount(): Int = currentList.size // ListAdapter uses currentList

    class ResultAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        private val imageView: ImageView = view.findViewById(R.id.imageView)

        fun bind(product: MoisturizerItem) {
            nameTextView.text = product.name
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(imageView)
        }
    }

    // DiffUtil callback to compare items
    class ProductDiffCallback : DiffUtil.ItemCallback<MoisturizerItem>() {
        override fun areItemsTheSame(oldItem: MoisturizerItem, newItem: MoisturizerItem): Boolean {
            // Compare by unique identifier, for example `id`
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: MoisturizerItem, newItem: MoisturizerItem): Boolean {
            // Check if the content of the items are the same
            return oldItem == newItem
        }
    }
}
