package com.capstone.skinpal.ui.onboarding

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.skinpal.R

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.onboarding_slide, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
        holder.animateImage(holder.image)
    }

    override fun getItemCount() = items.size

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.ivOnboardingImage)
        private val title = view.findViewById<TextView>(R.id.tvOnboardingTitle)
        private val description = view.findViewById<TextView>(R.id.tvOnboardingDescription)

        fun bind(item: OnboardingItem) {
            image.setImageResource(item.imageRes)
            title.text = item.title
            description.text = item.description
        }

        fun animateImage(imageView: ImageView) {
            // Buat animasi bergoyang
            val animator = ObjectAnimator.ofFloat(imageView, "translationX", -20f, 20f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
            animator.start()
        }
    }
}
