package com.everden.sfa.betfair

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject



@Controller("/betfair")
class BetfairController {

    private val LOG: Logger = LoggerFactory.getLogger(BetfairController::class.java)

    @Inject
    lateinit var betfairClient: BetfairClient


    @Post(uri="/listEventTypes", processes = [MediaType.APPLICATION_JSON])
    fun listEventTypes(@Body req : ApiFilterRequest) : HttpResponse<List<EventType>> {
        return makeFilterApiCall(req) { a, b -> b.listEventTypes(a)}
    }

    @Post(uri="/listCompetitions", processes = [MediaType.APPLICATION_JSON])
    fun listCompetitions(@Body req : ApiFilterRequest) : HttpResponse<List<Competition>> {
        return makeFilterApiCall(req) { a, b -> b.listCompetitions(a)}
    }

    @Post(uri="/listTimeRanges", processes = [MediaType.APPLICATION_JSON])
    fun listTimeRanges(@Body req : TimeRangeApiRequest) : HttpResponse<List<TimeRange>> {
        return makeFilterApiCall(req) { a, b -> b.listTimeRanges(a)}
    }

    @Post(uri="/listEvents", processes = [MediaType.APPLICATION_JSON])
    fun listEvents(@Body req : ApiFilterRequest) : HttpResponse<List<Event>> {
        return makeFilterApiCall(req) { a, b -> b.listEvents(a)}
    }

    @Post(uri="/listMarketType", processes = [MediaType.APPLICATION_JSON])
    fun marketType(@Body req : ApiFilterRequest) : HttpResponse<List<MarketType>> {
        return makeFilterApiCall(req) { a, b -> b.listMarketType(a)}
    }

    @Post(uri="/listCountries", processes = [MediaType.APPLICATION_JSON])
    fun listCountries(@Body req : ApiFilterRequest) : HttpResponse<List<Country>> {
        return makeFilterApiCall(req) { a, b -> b.listCountries(a)}
    }

    @Post(uri="/listVenues", processes = [MediaType.APPLICATION_JSON])
    fun listVenues(@Body req : ApiFilterRequest) : HttpResponse<List<Venue>> {
        return makeFilterApiCall(req) { a, b -> b.listVenues(a)}
    }

    @Post(uri="/listMarketCatalogue", processes = [MediaType.APPLICATION_JSON])
    fun listMarketCatalogue(@Body req : MarketCatalogueApiRequest) : HttpResponse<List<MarketCatalogue>> {
        return makeFilterApiCall(req) { a, b -> b.listMarketCatalogue(a)}
    }


    @Post(uri="/listMarketBook", processes = [MediaType.APPLICATION_JSON])
    fun listMarketBook(@Body req : ListMarketBookApiRequest) : HttpResponse<List<MarketBook>> {
        return try {
            val results = betfairClient.listMarketBook(req.request)
            return HttpResponse.ok(results)
        } catch (e: Exception) {
            LOG.error("makeCall error ", e)
            HttpResponse.badRequest()
        }
    }

    private fun <T: Any> makeFilterApiCall(@Body req : ApiFilterRequest, getData: (ExchangeFilterApiRequest, BetfairClient) -> List<T>) : HttpResponse<List<T>> {
        return try {
            val apiRequest = ExchangeFilterApiRequest(req)
            val results = getData.invoke(apiRequest, betfairClient)
            return HttpResponse.ok(results)
        } catch (e: Exception) {
            LOG.error("makeCall error ", e)
            HttpResponse.badRequest()
        }
    }

}







