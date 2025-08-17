package com.easyhooon.booksearch.core.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.easyhooon.booksearch.core.domain.model.Book
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.reflect.typeOf

sealed interface Route {
    @Serializable
    data class Detail(val book: Book) : Route {
        companion object {
            val typeMap = mapOf(
                typeOf<Book>() to BookType,
            )
        }
    }
}

sealed interface MainTabRoute : Route {
    @Serializable
    data object Search : MainTabRoute

    @Serializable
    data object Favorites : MainTabRoute
}

val BookType = object : NavType<Book>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): Book? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): Book {
        val decodedValue = URLDecoder.decode(value, "UTF-8")
        return Json.decodeFromString(decodedValue)
    }

    override fun put(bundle: Bundle, key: String, value: Book) {
        bundle.putString(key, Json.encodeToString(Book.serializer(), value))
    }

    override fun serializeAsValue(value: Book): String {
        val jsonString = Json.encodeToString(Book.serializer(), value)
        return URLEncoder.encode(jsonString, "UTF-8")
    }
}
