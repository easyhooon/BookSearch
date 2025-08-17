package com.easyhooon.booksearch.core.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.easyhooon.booksearch.core.common.model.BookUiModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.reflect.typeOf

sealed interface Route {
    @Serializable
    data class Detail(val book: BookUiModel) : Route {
        companion object {
            val typeMap = mapOf(
                typeOf<BookUiModel>() to BookType,
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

val BookType = object : NavType<BookUiModel>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): BookUiModel? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): BookUiModel {
        val decodedValue = URLDecoder.decode(value, "UTF-8")
        return Json.decodeFromString(decodedValue)
    }

    override fun put(bundle: Bundle, key: String, value: BookUiModel) {
        bundle.putString(key, Json.encodeToString(BookUiModel.serializer(), value))
    }

    override fun serializeAsValue(value: BookUiModel): String {
        val jsonString = Json.encodeToString(BookUiModel.serializer(), value)
        return URLEncoder.encode(jsonString, "UTF-8")
    }
}
