package com.example.moengageassignment.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moengageassignment.R
import com.example.moengageassignment.databinding.ActivityMainBinding
import com.example.moengageassignment.databinding.SortViewBinding
import com.example.moengageassignment.models.Article
import com.example.moengageassignment.utilities.NetworkUtils
import com.example.moengageassignment.utilities.NewsAdapter
import com.example.moengageassignment.utilities.Resource
import com.example.moengageassignment.utilities.SortOrder
import com.example.moengageassignment.viewmodels.NewsViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsViewModel: NewsViewModel by viewModels()
    private lateinit var sortBottomSheetDialog: BottomSheetDialog
    private lateinit var sortViewBinding: SortViewBinding
    private var lastCheckedRadioButtonId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        newsViewModel.news.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Handle success, update UI
                    val newsList = resource.data
                    setupRecyclerView(newsList)
                    binding.progressBar.visibility = View.GONE
                }

                is Resource.Error -> {
                    // Handle error, show error message
                    val errorMessage = resource.message ?: "An error occurred"
                    binding.progressBar.visibility = View.GONE
                    showToast(this, errorMessage)


                }

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE

                }
            }
        }

        if (NetworkUtils.isNetworkAvailable(this)) {
            // If internet is available, fetch data from the internet
            newsViewModel.getNews(true)
        } else {
            showToast(this, "Internet not available, loading data from local storage")
            newsViewModel.getNews(false)
        }

        binding.sortButton.setOnClickListener {
            showSortView()
        }

        newsViewModel.searchLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                // Hide the progress bar or loading indicator
                binding.progressBar.visibility = View.GONE
            }
        }


        var searchJob: Job? = null

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Cancel the previous search job if it's still running
                searchJob?.cancel()

                // Start a new coroutine with debounce
                searchJob = lifecycleScope.launch {
                    delay(1000) // Adjust the delay time as needed

                    // Perform the search after the delay
                    newsViewModel.searchNews(newText.orEmpty())
                }

                return true
            }
        })
        initSortBottomSheetDialog()
        handleIntent(intent)
        getToken()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            if (it.hasExtra("moEngage")) {
                // Handle data payload
                val dataValue = it.getStringExtra("moEngage")
            }
        }
    }

    private fun setupRecyclerView(newsList: List<Article>?) {

        newsAdapter = NewsAdapter(newsList)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = newsAdapter
        }
    }

    private fun initSortBottomSheetDialog() {
        sortViewBinding = SortViewBinding.inflate(layoutInflater)

        sortBottomSheetDialog = BottomSheetDialog(this)
        sortBottomSheetDialog.setContentView(sortViewBinding.root)
        sortBottomSheetDialog.dismissWithAnimation = true
        lastCheckedRadioButtonId = -1
        initSortPageViews()
    }

    private fun initSortPageViews() {

        sortViewBinding.btApply.setOnClickListener {
            hideSortView()

            val checkedId = sortViewBinding.sortRadioGroup.checkedRadioButtonId

            if (lastCheckedRadioButtonId == checkedId) {
                return@setOnClickListener
            }

            when (checkedId) {
                R.id.sortNewToOld -> {
                    newsViewModel.setSortOrder(SortOrder.NEW_TO_OLD)
                }

                R.id.sortOldToNew -> {
                    newsViewModel.setSortOrder(SortOrder.OLD_TO_NEW)
                }
            }

            lastCheckedRadioButtonId = checkedId
        }
    }

    private fun showSortView() {
        sortBottomSheetDialog.show()
    }

    private fun hideSortView() {
        sortBottomSheetDialog.dismiss()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("Token", "FCM Token: $token")
        })
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}