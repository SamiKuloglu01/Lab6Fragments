package com.nexustech.articlesearch3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

private const val API_KEY = "kGL5cx5iXejBt1BVr9AeHV6Pk9qUrnLQ"

class BookFragment : Fragment(), OnListFragmentInteractionListener {

    private lateinit var progressBar: ContentLoadingProgressBar
    private lateinit var recyclerView: RecyclerView
    private var bookList: List<BestSellerBook>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book, container, false)

        progressBar = view.findViewById(R.id.progress)
        recyclerView = view.findViewById(R.id.list)

        // Set up the GridLayoutManager for the RecyclerView
        recyclerView.layoutManager = GridLayoutManager(view.context, 2)

        // Restore saved instance state
        savedInstanceState?.let {
            bookList = it.getParcelableArrayList("bookList")
            bookList?.let { books ->
                updateAdapter(books) // Update with saved data
            }
        }

        // If no data is available, fetch the data
        if (bookList == null) {
            (activity as? MainActivity)?.updateToolbar(R.drawable.ic_book, "Books")
            fetchBookData() // Fetch data if not already loaded
        } else {
            // If data is already loaded, hide progress bar
            progressBar.hide()
        }

        return view
    }

    // Method to fetch the book data
    private fun fetchBookData() {
        // Only show progress bar if data is not already loaded
        if (bookList == null) {
            progressBar.show()
        }

        val client = AsyncHttpClient()
        val url = "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json?api-key=$API_KEY"
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                progressBar.hide() // Hide progress bar once data is fetched
                try {
                    val resultsJSON = json.jsonObject.getJSONObject("results").getJSONArray("books")
                    val models: List<BestSellerBook> = BestSellerBook.fromJson(resultsJSON)
                    updateAdapter(models) // Update RecyclerView with the new data
                    bookList = models
                    Log.d("BestSellerBooksFragment", "Response successful")
                } catch (e: Exception) {
                    Log.e("BestSellerBooksFragment", "Failed to parse JSON: $e")
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, t: Throwable?) {
                progressBar.hide() // Hide progress bar on failure
                t?.message?.let { Log.e("BestSellerBooksFragment", "Error: $errorResponse") }
            }
        })
    }

    // Method to update the RecyclerView with the book data
    private fun updateAdapter(models: List<BestSellerBook>) {
        val adapter = BestSellerBooksRecyclerViewAdapter(models, this@BookFragment)
        recyclerView.adapter = adapter
    }

    // Save the list of books to the instance state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("bookList", ArrayList(bookList))
    }

    // Handle item click events
    override fun onItemClick(item: BestSellerBook) {
        Toast.makeText(context, "Selected book: " + item.title, Toast.LENGTH_LONG).show()
    }
}
