package com.easyhooon.booksearch.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import com.easyhooon.booksearch.core.domain.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BookRepository,
) : ViewModel()