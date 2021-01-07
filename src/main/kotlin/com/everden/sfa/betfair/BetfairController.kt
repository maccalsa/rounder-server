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
    fun listEventTypes(@Body req : ApiRequest) : HttpResponse<List<EventType>> {
        return makeApiCall(req) {a, b, c -> c.listEventTypes(SESSION_KEY, a, b)}
    }

    @Post(uri="/listCompetitions", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listCompetitions(@Body req : ApiRequest) : HttpResponse<List<Competition>> {
        return makeApiCall(req) {a, b, c -> c.listCompetitions(SESSION_KEY, a, b)}
    }

    @Post(uri="/listTimeRanges", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listTimeRanges(@Body req : ApiRequest) : HttpResponse<List<TimeRange>> {
        return makeApiCall(req) {a, b, c -> c.listTimeRanges(SESSION_KEY, a, b)}
    }

    @Post(uri="/listEvents", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun listEvents(@Body req : ApiRequest) : HttpResponse<List<Event>> {
        return makeApiCall(req) {a, b, c -> c.listEvents(SESSION_KEY, a, b)}
    }

    @Post(uri="/marketType", consumes = [MediaType.APPLICATION_JSON], produces = [MediaType.APPLICATION_JSON])
    fun marketType(@Body req : ApiRequest) : HttpResponse<List<MarketType>> {
        return makeApiCall(req) {a, b, c -> c.marketType(SESSION_KEY, a, b)}
    }

    private fun <T: Any> makeApiCall(@Body req : ApiRequest, getData: (String, ExchangeApiRequest, BetfairClient) -> List<T>) : HttpResponse<List<T>> {
        return try {
            val apiRequest = ExchangeApiRequest(req.filter, req.granularity)
            val results = getData.invoke(req.appKey, apiRequest, betfairClient)
            return HttpResponse.ok(results)
        } catch (e: Exception) {
            LOG.error("listTimeRanges error ", e)
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
                       @Body req : ExchangeApiRequest) : List<EventType>

    @Post("listCompetitions/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listCompetitions(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeApiRequest) : List<Competition>

    @Post("listTimeRanges/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listTimeRanges(@Header("X-Authentication") authentication : String,
                         @Header("X-Application") applicationKey: String,
                         @Body req : ExchangeApiRequest) : List<TimeRange>

    @Post("listEvents/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun listEvents(@Header("X-Authentication") authentication : String,
                       @Header("X-Application") applicationKey: String,
                       @Body req : ExchangeApiRequest) : List<Event>

    @Post("marketType/",
            produces = [MediaType.APPLICATION_JSON],
            consumes = [MediaType.APPLICATION_JSON])
    fun marketType(@Header("X-Authentication") authentication : String,
                   @Header("X-Application") applicationKey: String,
                   @Body req : ExchangeApiRequest) : List<MarketType>

}
