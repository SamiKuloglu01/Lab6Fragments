package com.nexustech.articlesearch3

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.Gson
import org.json.JSONArray

/**
 * The Model for storing a single book from the NY Times API.
 */
class BestSellerBook() : Parcelable {
    @SerializedName("rank")
    var rank: Int = 0
    @SerializedName("title")
    var title: String? = null
    @SerializedName("author")
    var author: String? = null
    @SerializedName("book_image")
    var bookImageUrl: String? = null
    @SerializedName("description")
    var description: String? = null
    @SerializedName("amazon_product_url")
    var amazonUrl: String? = null

    // Constructor for Parcelable
    constructor(parcel: Parcel) : this() {
        rank = parcel.readInt()
        title = parcel.readString()
        author = parcel.readString()
        bookImageUrl = parcel.readString()
        description = parcel.readString()
        amazonUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(rank)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(bookImageUrl)
        parcel.writeString(description)
        parcel.writeString(amazonUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BestSellerBook> {
        override fun createFromParcel(parcel: Parcel): BestSellerBook {
            return BestSellerBook(parcel)
        }

        override fun newArray(size: Int): Array<BestSellerBook?> {
            return arrayOfNulls(size)
        }

        /**
         * Factory method to convert a JSON array into a list of BestSellerBook objects
         */
        fun fromJson(jsonArray: JSONArray): List<BestSellerBook> {
            val books = mutableListOf<BestSellerBook>()
            val gson = Gson()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val book = gson.fromJson(jsonObject.toString(), BestSellerBook::class.java)
                books.add(book)
            }
            return books
        }
    }
}
