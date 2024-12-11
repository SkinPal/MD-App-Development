package com.capstone.skinpal.ui.home

import com.capstone.skinpal.R
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.databinding.FragmentHomeBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var userPreference: UserPreference
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory(Injection.provideRepository(requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreference = UserPreference(requireContext())

        setupUI()
        setupRecyclerView()
        observeArticles()
        setupSlider() // Tambahkan setupSlider untuk slider skin type
        binding.buttonProgress.setOnClickListener {
            navigateToHistoryFragment()
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = articleAdapter
        }
    }

    private fun setupSlider() {
        // Data untuk slider skin type
        val images = listOf(
            R.drawable.pic2, // Ganti dengan resource gambar Anda
            R.drawable.pic3,
            R.drawable.pic
        )
        val titles = listOf(
            "Oily Skin",
            "Dry Skin",
            "Combination Skin"
        )
        val descriptions = listOf(
            "Your skin produces excess oil, leading to shine and acne.",
            "Your skin feels tight and lacks moisture.",
            "Your skin is oily in some areas and dry in others."
        )

        // Inisialisasi adapter ViewPager2
        val skinTypeAdapter = SkinTypeAdapter(images, titles, descriptions)
        binding.skinTypeViewPager.adapter = skinTypeAdapter
    }

    private fun setupUI() {
        userPreference = UserPreference(requireContext())
        val session = userPreference.getSession()

        val userId = session?.user
        if (!userId.isNullOrEmpty()) {
            homeViewModel.fetchUserProfile()
        } else {
            Log.e("AccountFragment", "User ID kosong atau belum login")
        }

        homeViewModel.fetchUserProfile()
        val username = session?.user ?: getString(R.string.default_username)
        binding.tvName.text = getString(R.string.greeting_format, username)
        homeViewModel.userProfile.observe(viewLifecycleOwner) { profileResponse ->
            profileResponse?.data?.let { data ->
                Glide.with(this)
                    .load(data.profileImage)
                    .placeholder(R.drawable.icon_person)
                    .error(R.drawable.icon_person)
                    .into(binding.profile)
            }
        }
    }

    private fun observeArticles() {
        homeViewModel.getArticle().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvNoArticle.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val articles = result.data.takeLast(6)
                    if (articles.isEmpty()) {
                        binding.tvNoArticle.visibility = View.VISIBLE
                    } else {
                        binding.tvNoArticle.visibility = View.GONE
                        articleAdapter.submitList(articles)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoArticle.visibility = View.GONE
                }
            }
        }
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        // Placeholder untuk progress bar
    }

    fun navigateToHistoryFragment() {
        findNavController().navigate(
            R.id.navigation_reminder,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
