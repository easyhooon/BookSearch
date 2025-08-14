package com.easyhooon.booksearch.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class BookEntity(
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "contents")
    val contents: String,
    @ColumnInfo(name = "url")
    val url: String,
    @PrimaryKey
    @ColumnInfo(name = "isbn")
    val isbn: String,
    @ColumnInfo(name = "datetime")
    val datetime: String,
    @ColumnInfo(name = "authors")
    val authors: List<String>,
    @ColumnInfo(name = "publisher")
    val publisher: String,
    @ColumnInfo(name = "translators")
    val translators: List<String>,
    @ColumnInfo(name = "price")
    val price: String,
    @ColumnInfo(name = "sale_price")
    val salePrice: String,
    @ColumnInfo(name = "thumbnail")
    val thumbnail: String,
    @ColumnInfo(name = "status")
    val status: String
)
