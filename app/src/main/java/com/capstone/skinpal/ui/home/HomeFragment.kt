package com.capstone.skinpal.ui.home

import com.capstone.skinpal.R
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.skinpal.data.Result
import com.capstone.skinpal.data.UserPreference
import com.capstone.skinpal.data.local.entity.AnalysisEntity
import com.capstone.skinpal.databinding.FragmentHomeBinding
import com.capstone.skinpal.di.Injection
import com.capstone.skinpal.ui.ViewModelFactory

class HomeFragment : Fragment() {

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
       // observeUserData()
        setupRecyclerView()
        //observeArticles()
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

    private fun setupUI() {
        // Get username from SharedPreferences
        val session = userPreference.getSession()
        val username = session.user ?: getString(R.string.default_user)

        // Update greeting text
        binding.tvName.text = getString(R.string.greeting_format, username)
        homeViewModel.getAnalysisByUserId(username).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    // Show loading indicator
                    showLoading(true)
                }
                is Result.Success -> {
                    // Display analysis data
                    showLoading(false)
                    val analysis = result.data
                    displaySkinType(analysis)
                }
                is Result.Error -> {
                    // Show error message
                    showLoading(false)
                    showError(result.error)
                }
            }
        }
    }

    private fun displaySkinType(analysis: AnalysisEntity) {
        binding.skinCondition.text = analysis.skinType
        // Populate UI components with analysis data
        Log.d("AnalysisFragment", "Analysis: $analysis")
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        // Toggle progress bar or loading indicator visibility
        // Example:
        if (isLoading) {
            // binding.progressBar.visibility = View.VISIBLE
        } else {
            // binding.progressBar.visibility = View.GONE
        }
    }

    /*private fun observeUserData() {
        val userId = userPreference.getSession().user
        userId?.let { uid ->
            homeViewModel.(uid).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        result.data?.let { analysis ->
                            binding.skinCondition.text = analysis.skinType
                        } ?: run {
                            binding.skinCondition.text = getString(R.string.no_skin_type)
                        }
                    }
                    is Result.Error -> {
                        binding.skinCondition.text = getString(R.string.error_loading_skin_type)
                    }
                    is Result.Loading -> {
                        binding.skinCondition.text = getString(R.string.loading)
                    }
                }
            }
        } ?: run {
            binding.skinCondition.text = getString(R.string.please_login)
        }
    }*/


    /*private fun observeArticles() {
        homeViewModel.getArticle().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvNoArticle.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val articles = result.data.takeLast(3)
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
    }*/

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
