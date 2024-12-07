package com.capstone.skinpal.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.skinpal.R
import com.capstone.skinpal.data.local.entity.ProductEntity
import com.capstone.skinpal.databinding.ItemProductBinding

class MoisturizerAdapter : ListAdapter<ProductEntity, MoisturizerAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductEntity) {
            binding.titleProducts.text = product.name
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(binding.productPhoto)
            /*itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(product.url)
                itemView.context.startActivity(intent)
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ProductEntity> =
            object : DiffUtil.ItemCallback<ProductEntity>() {
                override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
