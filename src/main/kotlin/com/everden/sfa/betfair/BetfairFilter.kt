package com.everden.sfa.betfair

import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.ClientFilterChain
import io.micronaut.http.filter.HttpClientFilter
import org.reactivestreams.Publisher
import javax.inject.Inject

@Filter(patterns = ["/**"])
class BetfairFilter : HttpClientFilter {

    @Inject
    lateinit var authenticate: Authenticate

    override fun doFilter(request: MutableHttpRequest<*>, chain: ClientFilterChain): Publisher<out HttpResponse<*>> {
        request.headers["X-Authentication"] = authenticate.sessionKey
        request.headers["X-Application"] = authenticate.appKey
        return chain.proceed(request)
    }


}