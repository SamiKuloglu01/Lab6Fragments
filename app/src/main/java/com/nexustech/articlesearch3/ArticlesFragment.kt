package com.nexustech.articlesearch3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "ArticlesFragment"
private const val ARTICLE_SEARCH_URL =
    "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=TnazoyuRWyYtbbP1nixP5G50s6PuioA7"

class ArticlesFragment : Fragment() {
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private val articles = mutableListOf<Article>()
    private var searchQuery: String? = null
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView started")
        val view = inflater.inflate(R.layout.fragment_articles, container, false)
        (activity as? MainActivity)?.updateToolbar(R.drawable.ic_article, "Articles")
        Log.d(TAG, "Layout inflated successfully")

        initializeViews(view)
        setupRecyclerView()
        setupSearchView()

        // Restore state
        savedInstanceState?.let {
            val savedArticles = it.getParcelableArrayList<Article>("articles")
            searchQuery = it.getString("searchQuery")
            articles.clear()
            savedArticles?.let { articles.addAll(it) }
            searchQuery?.let { searchArticles(it) }
            isDataLoaded = savedArticles != null && savedArticles.isNotEmpty() // Mark as loaded
        }

        // Only fetch articles if they have not been loaded yet
        if (!isDataLoaded) {
            fetchArticlesFromApi()  // Ensure to fetch new articles if necessary
        } else {
            progressBar.visibility = View.GONE // Hide progress bar if data is already loaded
        }

        return view
    }

    private fun initializeViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        recyclerView = view.findViewById(R.id.articles)
        searchView = view.findViewById(R.id.search_view)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(requireContext(), articles)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchQuery = it  // Store the search query
                    searchArticles(it)
                }
                return true
            }
        })
    }

    private fun fetchArticlesFromApi() {
        progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.get(ARTICLE_SEARCH_URL, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e(TAG, "Failed to fetch articles: $statusCode")
                progressBar.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    "Failed to load articles. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                handleApiSuccess(json)
            }
        })
    }

    private fun handleApiSuccess(json: JsonHttpResponseHandler.JSON) {
        try {
            val parsedJson = Json { ignoreUnknownKeys = true }
                .decodeFromString<SearchNewsResponse>(json.jsonObject.toString())
            val apiArticles = parsedJson.response?.docs ?: emptyList()
            val filteredArticles = apiArticles.filter { it.multimedia?.isNotEmpty() == true }
            updateArticles(filteredArticles)
        } catch (e: JSONException) {
            Log.e(TAG, "Exception: $e")
            progressBar.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun updateArticles(filteredArticles: List<Article>) {
        articles.clear()
        articles.addAll(filteredArticles)
        articleAdapter.notifyDataSetChanged()
        progressBar.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
        isDataLoaded = true  // Mark data as loaded
    }

    private fun searchArticles(query: String) {
        val filteredArticles = articles.filter {
            it.headline?.main?.contains(query, ignoreCase = true) == true
        }
        articleAdapter.updateArticles(filteredArticles)
    }

    // Save state (articles and search query)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("articles", ArrayList(articles)) // Save articles
        outState.putString("searchQuery", searchQuery) // Save current search query
    }
}
