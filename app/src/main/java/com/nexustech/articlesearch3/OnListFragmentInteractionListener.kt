package com.nexustech.articlesearch3

/**
 * This interface is used by the [BestSellerBooksRecyclerViewAdapter] to ensure
 * it has an appropriate Listener.
 *
 * In this app, it's implemented by [BookFragment]
 */
interface OnListFragmentInteractionListener {
    fun onItemClick(item: BestSellerBook)
}
