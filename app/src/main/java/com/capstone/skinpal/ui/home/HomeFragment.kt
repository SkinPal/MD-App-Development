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
import com.bumptech.glide.Glide
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.databinding.FragmentHomeBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.BaseFragment
import com.capstone.skinpal.ui.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HomeFragment : BottomSheetDialogFragment(), BaseFragment {

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
        setupSlider()
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
        val images = listOf(
            R.drawable.oily,
            R.drawable.dry,
            R.drawable.combi
        )
        val titles = listOf(
            getString(R.string.oily_skin),
            getString(R.string.dry_skin),
            getString(R.string.combination_skin)
        )
        val descriptions = listOf(
            getString(R.string.oily_skin_desc),
            getString(R.string.dry_skin_desc),
            getString(R.string.combi_skin_desc)
        )

        val skinTypeAdapter = SkinTypeAdapter(images, titles, descriptions)
        binding.skinTypeViewPager.adapter = skinTypeAdapter
    }

    private fun setupUI() {
        userPreference = UserPreference(requireContext())
        val session = userPreference.getSession()

        val userId = session.user
        if (!userId.isNullOrEmpty()) {
            homeViewModel.fetchUserProfile()
        } else {
            handleApiError(getString(R.string.user_not_authenticated), requireContext())
            Log.e(getString(R.string.accountfragment), getString(R.string.user_id_kosong_atau_belum_login))
        }

        homeViewModel.fetchUserProfile()
        val username = session.user ?: getString(R.string.default_username)
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
                    handleApiError(result.error, requireContext())
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
