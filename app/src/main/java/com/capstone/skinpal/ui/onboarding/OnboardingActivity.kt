package com.capstone.skinpal.ui.onboarding

import android.content.Intent
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.capstone.skinpal.R
import android.os.Bundle
import com.capstone.skinpal.ui.login.LoginActivity
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.view.animation.DecelerateInterpolator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var indicatorLayout: LinearLayout
    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.skinpal_pink,
            "Welcome to SkinPal",
            "Your personalized skincare assistant!. SkinPal is here to revolutionize your skincare journey. From personalized product recommendations to tracking your skin's progress, we’ve got everything you need to achieve healthy, glowing skin. Let SkinPal be your trusted companion in understanding and caring for your unique skin needs."
        ),
        OnboardingItem(
            R.drawable.skintype_ob,
            "Analyze Your Skin",
            "Get insights about your skin condition and skincare routine ✨. SkinPal uses advanced facial scanning technology to analyze your skin and provide accurate results tailored to your needs. Discover personalized recommendations and take the guesswork out of your skincare journey."
        ),
        OnboardingItem(
            R.drawable.pic7,
            "Stay on Track",
            "Receive your daily reminders for skincare routine ⏰. SkinPal helps you build consistency by sending notifications for your morning and evening skincare steps. Stay committed, stay glowing, and let us guide you toward healthier skin every day."
        ),
        OnboardingItem(
            R.drawable.pic10,
            "Monitor Your Skin Condition",
            "Upload your skin photo and get detailed progress updates after 1 month! With SkinPal, you can track changes in your skin condition over time and see how your skincare routine makes a difference. Stay informed and motivated on your journey to healthier skin."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)
        indicatorLayout = findViewById(R.id.indicatorLayout)

        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        setupIndicators()
        updateIndicators(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                btnNext.text = if (position == onboardingItems.size - 1) "Get Started" else "Next"
                animatePage(position)
            }
        })

        btnNext.setOnClickListener {
            if (viewPager.currentItem < onboardingItems.size - 1) {
                viewPager.currentItem += 1
            } else {
                navigateToMainScreen()
            }
        }
    }

    private fun animatePage(position: Int) {
        val currentItemView = (viewPager.getChildAt(0) as ViewGroup?)?.getChildAt(position) as? ImageView

        currentItemView?.let {

            it.translationX = 0f

            val animatorX = ObjectAnimator.ofFloat(it, "translationX", -50f, 0f)
            animatorX.duration = 400
            animatorX.interpolator = DecelerateInterpolator()

            val animatorY = ObjectAnimator.ofFloat(it, "translationY", -50f, 0f)
            animatorY.duration = 400
            animatorY.interpolator = DecelerateInterpolator()

            AnimatorSet().apply {
                playTogether(animatorX, animatorY)
                start()
            }
        }
    }

    private fun setupIndicators() {
        val indicators = Array(onboardingItems.size) { ImageView(this) }
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            marginEnd = 8
        }

        indicators.forEach { indicator ->
            indicator.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive))
            indicator.layoutParams = params
            indicatorLayout.addView(indicator)
        }
    }

    private fun updateIndicators(index: Int) {
        for (i in 0 until indicatorLayout.childCount) {
            val indicator = indicatorLayout.getChildAt(i) as ImageView
            indicator.setImageDrawable(
                ContextCompat.getDrawable(this, if (i == index) R.drawable.indicator_active else R.drawable.indicator_inactive)
            )
        }
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
