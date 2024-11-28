package com.capstone.skinpal.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.skinpal.R
import com.capstone.skinpal.data.local.entity.ArticleEntity
import com.capstone.skinpal.databinding.ItemProductBinding

class ProductAdapter : ListAdapter<ArticleEntity, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ArticleEntity) {
            binding.productTitle.text = product.title
            Glide.with(itemView.context)
                .load(product.urlToImage)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
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
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ArticleEntity> =
            object : DiffUtil.ItemCallback<ArticleEntity>() {
                override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
