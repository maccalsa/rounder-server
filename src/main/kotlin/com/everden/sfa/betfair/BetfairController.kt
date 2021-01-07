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

    @Post(uri="/listEventTypes",
            consumes = [MediaType.APPLICATION_JSON],
            produces = [MediaType.APPLICATION_JSON]
    )
    fun listEventTypes(@Body req : ApiRequest) : HttpResponse<List<EventType>> {

        return try {
            val apiRequest = ExchangeApiRequest(req.filter, req.granularity)
            val results = betfairClient.listEventTypes(SESSION_KEY, req.appKey, apiRequest)
            return HttpResponse.ok(results)
        } catch (e: IllegalStateException) {
            LOG.error("listEventTypes error ", e)
            HttpResponse.badRequest()
        }
    }

    @Post(uri="/listCompetitions",
            consumes = [MediaType.APPLICATION_JSON],
            produces = [MediaType.APPLICATION_JSON]
    )
    fun listCompetitions(@Body req : ApiRequest) : HttpResponse<List<Competition>> {

        return try {
            val apiRequest = ExchangeApiRequest(req.filter, req.granularity)
            val results = betfairClient.listCompetitions(SESSION_KEY, req.appKey, apiRequest)
            return HttpResponse.ok(results)
        } catch (e: Exception) {
            LOG.error("listCompetitions error ", e)
            HttpResponse.badRequest()
        }
    }

    @Post(uri="/listTimeRanges",
            consumes = [MediaType.APPLICATION_JSON],
            produces = [MediaType.APPLICATION_JSON]
    )
    fun listTimeRanges(@Body req : ApiRequest) : HttpResponse<List<TimeRange>> {

        return try {
            val apiRequest = ExchangeApiRequest(req.filter, req.granularity)
            val results = betfairClient.listTimeRanges(SESSION_KEY, req.appKey, apiRequest)
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

}
