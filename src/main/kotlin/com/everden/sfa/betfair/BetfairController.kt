package com.everden.sfa.betfair

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject

@Controller("/betfair")
class BetfairController {

    private val LOG: Logger = LoggerFactory.getLogger(BetfairController::class.java)

    @Value("\${rounder-server.credentials.token:}")
    var SESSION_KEY : String = ""

    @Inject
    lateinit var authenticate : Authenticate

    @Inject
    lateinit var betfairClient: BetfairClient

    @Post(uri="/login",
            consumes = [MediaType.APPLICATION_JSON],
            produces = [MediaType.APPLICATION_JSON]
    )
    fun login(@Body req : LoginRequest) : HttpResponse<LoginResponse?> {
        return try {
            val result = authenticate.authenticate(
                    certificatePassword = req.certificatePassword,
                    password = req.password,
                    appKey = req.appKey,
                    certificateLocation = req.certificateLocation,
                    username = req.username)
            HttpResponse.created(result)
        } catch (e: IllegalStateException) {
            var error : LoginResponse? = LoginResponse(e.message, "APP_FAILURE")
            HttpResponse.badRequest(error)
        }
    }

    @Post(uri="/listEventTypes", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listEventTypes(@Body req : ApiFilterRequest) : HttpResponse<List<EventType>> {
        return makeFilterApiCall(req) { a, b, c -> c.listEventTypes(SESSION_KEY, a, b)}
    }

    @Post(uri="/listCompetitions", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listCompetitions(@Body req : ApiFilterRequest) : HttpResponse<List<Competition>> {
        return makeFilterApiCall(req) { a, b, c -> c.listCompetitions(SESSION_KEY, a, b)}
    }

    @Post(uri="/listTimeRanges", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listTimeRanges(@Body req : TimeRangeApiRequest) : HttpResponse<List<TimeRange>> {
        return makeFilterApiCall(req) { a, b, c -> c.listTimeRanges(SESSION_KEY, a, b)}
    }

    @Post(uri="/listEvents", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listEvents(@Body req : ApiFilterRequest) : HttpResponse<List<Event>> {
        return makeFilterApiCall(req) { a, b, c -> c.listEvents(SESSION_KEY, a, b)}
    }

    @Post(uri="/listMarketType", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun marketType(@Body req : ApiFilterRequest) : HttpResponse<List<MarketType>> {
        return makeFilterApiCall(req) { a, b, c -> c.listMarketType(SESSION_KEY, a, b)}
    }

    @Post(uri="/listCountries", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listCountries(@Body req : ApiFilterRequest) : HttpResponse<List<Country>> {
        return makeFilterApiCall(req) { a, b, c -> c.listCountries(SESSION_KEY, a, b)}
    }

    @Post(uri="/listVenues", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listVenues(@Body req : ApiFilterRequest) : HttpResponse<List<Venue>> {
        return makeFilterApiCall(req) { a, b, c -> c.listVenues(SESSION_KEY, a, b)}
    }

    @Post(uri="/listMarketCatalogue", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listMarketCatalogue(@Body req : MarketCatalogueApiRequest) : HttpResponse<List<MarketCatalogue>> {
        return makeFilterApiCall(req) { a, b, c -> c.listMarketCatalogue(SESSION_KEY, a, b)}
    }


    @Post(uri="/listMarketBook", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listMarketBook(@Body req : ListMarketBookApiRequest) : HttpResponse<List<MarketBook>> {
        return try {
            val results = betfairClient.listMarketBook(SESSION_KEY, req.appKey, req.request)
            return HttpResponse.ok(results)
        } catch (e: Exception) {
            LOG.error("makeCall error ", e)
            HttpResponse.badRequest()
        }
    }

    private fun <T: Any> makeFilterApiCall(@Body req : ApiFilterRequest, getData: (String, ExchangeFilterApiRequest, BetfairClient) -> List<T>) : HttpResponse<List<T>> {
        return try {
            val apiRequest = ExchangeFilterApiRequest(req)
            val results = getData.invoke(req.appKey, apiRequest, betfairClient)
            return HttpResponse.ok(results)
        } catch (e: Exception) {
            LOG.error("makeCall error ", e)
            HttpResponse.badRequest()
        }
    }

}

@Client("https://api.betfair.com/exchange/betting/rest/v1.0/")
interface BetfairClient {

    @Post("listEventTypes/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listEventTypes(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeFilterApiRequest) : List<EventType>

    @Post("listCompetitions/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listCompetitions(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeFilterApiRequest) : List<Competition>

    @Post("listTimeRanges/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listTimeRanges(@Header("X-Authentication") authentication : String,
                         @Header("X-Application") applicationKey: String,
                         @Body req : ExchangeFilterApiRequest) : List<TimeRange>

    @Post("listEvents/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listEvents(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeFilterApiRequest) : List<Event>

    @Post("listMarketType/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listMarketType(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeFilterApiRequest) : List<MarketType>

    @Post("listCountries/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listCountries(@Header("X-Authentication") authentication : String,
                      @Header("X-Application") applicationKey: String,
                      @Body req : ExchangeFilterApiRequest) : List<Country>

    @Post("listVenues/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listVenues(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeFilterApiRequest) : List<Venue>

    @Post("listMarketCatalogue/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listMarketCatalogue(@Header("X-Authentication") authentication : String,
                   @Header("X-Application") applicationKey: String,
                   @Body req : ExchangeFilterApiRequest) : List<MarketCatalogue>

    @Post("listMarketBook/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listMarketBook(@Header("X-Authentication") authentication : String,
                            @Header("X-Application") applicationKey: String,
                            @Body req : ListMarketBookRequest) : List<MarketBook>



}
