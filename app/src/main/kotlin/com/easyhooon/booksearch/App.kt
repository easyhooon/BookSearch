package com.easyhooon.booksearch

import android.app.Application
import com.easyhooon.booksearch.core.common.SwrClientFactory
import dagger.hilt.android.HiltAndroidApp
import soil.query.AndroidMemoryPressure
import soil.query.AndroidNetworkConnectivity
import soil.query.AndroidWindowVisibility
import soil.query.QueryOptions
import soil.query.SwrCachePlus
import soil.query.SwrCachePlusPolicy
import soil.query.SwrCacheScope
import soil.query.SwrClientPlus
import soil.query.annotation.ExperimentalSoilQueryApi

@HiltAndroidApp
class App : Application(), SwrClientFactory {

    @OptIn(ExperimentalSoilQueryApi::class)
    override val queryClient: SwrClientPlus by lazy {
        SwrCachePlus(
            policy = SwrCachePlusPolicy(
                coroutineScope = SwrCacheScope(),
                queryOptions = QueryOptions(
                    logger = { println(it) }
                ),
                memoryPressure = AndroidMemoryPressure(this),
                networkConnectivity = AndroidNetworkConnectivity(this),
                windowVisibility = AndroidWindowVisibility()
            )
        )
    }
}
