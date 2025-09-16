package com.easyhooon.booksearch.core.common

import soil.query.SwrClientPlus

interface SwrClientFactory {
    val queryClient: SwrClientPlus
}