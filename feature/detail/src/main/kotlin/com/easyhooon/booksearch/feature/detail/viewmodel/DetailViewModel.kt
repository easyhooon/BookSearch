package com.easyhooon.booksearch.feature.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.easyhooon.booksearch.core.domain.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel()