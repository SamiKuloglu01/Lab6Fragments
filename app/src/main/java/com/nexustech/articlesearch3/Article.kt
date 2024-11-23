package com.nexustech.articlesearch3
import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchNewsResponse(
    @SerialName("response") val response: BaseResponse?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(BaseResponse::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(response, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SearchNewsResponse> {
        override fun createFromParcel(parcel: Parcel): SearchNewsResponse {
            return SearchNewsResponse(parcel)
        }

        override fun newArray(size: Int): Array<SearchNewsResponse?> {
            return arrayOfNulls(size)
        }
    }
}

@Serializable
data class BaseResponse(
    @SerialName("docs") val docs: List<Article>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createTypedArrayList(Article.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(docs)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BaseResponse> {
        override fun createFromParcel(parcel: Parcel): BaseResponse {
            return BaseResponse(parcel)
        }

        override fun newArray(size: Int): Array<BaseResponse?> {
            return arrayOfNulls(size)
        }
    }
}

@Serializable
data class Article(
    @SerialName("abstract") val abstract: String?,
    @SerialName("headline") val headline: Headline?,
    @SerialName("byline") val byline: Byline?,
    @SerialName("multimedia") val multimedia: List<MultiMedia>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Headline::class.java.classLoader),
        parcel.readParcelable(Byline::class.java.classLoader),
        parcel.createTypedArrayList(MultiMedia.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(abstract)
        parcel.writeParcelable(headline, flags)
        parcel.writeParcelable(byline, flags)
        parcel.writeTypedList(multimedia)
    }

    override fun describeContents(): Int = 0

    val mediaImageUrl: String
        get() {
            val url = multimedia?.firstOrNull { !it.url.isNullOrEmpty() }?.url
            return if (!url.isNullOrEmpty()) {
                if (url.startsWith("http")) url else "https://www.nytimes.com/$url"
            } else {
                "" // Return an empty string if there’s no valid URL
            }
        }

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }
}

@Serializable
data class Headline(
    @SerialName("main") val main: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(main)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Headline> {
        override fun createFromParcel(parcel: Parcel): Headline {
            return Headline(parcel)
        }

        override fun newArray(size: Int): Array<Headline?> {
            return arrayOfNulls(size)
        }
    }
}

@Serializable
data class Byline(
    @SerialName("original") val original: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(original)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Byline> {
        override fun createFromParcel(parcel: Parcel): Byline {
            return Byline(parcel)
        }

        override fun newArray(size: Int): Array<Byline?> {
            return arrayOfNulls(size)
        }
    }
}

@Serializable
data class MultiMedia(
    @SerialName("url") val url: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MultiMedia> {
        override fun createFromParcel(parcel: Parcel): MultiMedia {
            return MultiMedia(parcel)
        }

        override fun newArray(size: Int): Array<MultiMedia?> {
            return arrayOfNulls(size)
        }
    }
}
