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
            getString(R.string.onboarding_page1),
            getString(R.string.onboarding_desc1)
        ),
        OnboardingItem(
            R.drawable.skintype_ob,
            getString(R.string.onboarding_page2),
            getString(R.string.onboarding_desc2)
        ),
        OnboardingItem(
            R.drawable.pic7,
            getString(R.string.onboarding_page3),
            getString(R.string.onboarding_desc3)
        ),
        OnboardingItem(
            R.drawable.pic10,
            getString(R.string.onboarding_page4),
            getString(R.string.onboarding_desc4)
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
