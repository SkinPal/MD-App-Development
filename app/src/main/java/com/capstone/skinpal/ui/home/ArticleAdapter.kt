package com.capstone.skinpal.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capstone.skinpal.databinding.ItemArticleBinding
import com.capstone.skinpal.R
import com.capstone.skinpal.data.local.entity.ArticleEntity

class ArticleAdapter : ListAdapter<ArticleEntity, ArticleAdapter.ArticleViewHolder>(DIFF_CALLBACK) {

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleEntity) {
            binding.tvItemTitle.text = article.title
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.imgPoster)
            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(article.url)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ArticleEntity> =
            object : DiffUtil.ItemCallback<ArticleEntity>() {
                override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                    return oldItem.url == newItem.url
                }

                override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
