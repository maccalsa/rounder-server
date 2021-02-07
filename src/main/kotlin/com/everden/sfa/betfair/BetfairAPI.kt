package com.everden.sfa.betfair

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import java.util.*

@Client("https://api.betfair.com/exchange/betting/rest/v1.0/")
interface BetfairClient {

    @Post("listEventTypes/", processes = [MediaType.APPLICATION_JSON])
    fun listEventTypes(@Body req : ExchangeFilterApiRequest) : List<EventType>

    @Post("listCompetitions/",processes = [MediaType.APPLICATION_JSON])
    fun listCompetitions(@Body req : ExchangeFilterApiRequest) : List<Competition>

    @Post("listTimeRanges/",processes = [MediaType.APPLICATION_JSON])
    fun listTimeRanges(@Body req : ExchangeFilterApiRequest) : List<TimeRange>

    @Post("listEvents/",processes = [MediaType.APPLICATION_JSON])
    fun listEvents(@Body req : ExchangeFilterApiRequest) : List<Event>

    @Post("listMarketType/",processes = [MediaType.APPLICATION_JSON])
    fun listMarketType(@Body req : ExchangeFilterApiRequest) : List<MarketType>

    @Post("listCountries/",processes = [MediaType.APPLICATION_JSON])
    fun listCountries(@Body req : ExchangeFilterApiRequest) : List<Country>

    @Post("listVenues/",processes = [MediaType.APPLICATION_JSON])
    fun listVenues(@Body req : ExchangeFilterApiRequest) : List<Venue>

    @Post("listMarketCatalogue/",processes = [MediaType.APPLICATION_JSON])
    fun listMarketCatalogue(@Body req : ExchangeFilterApiRequest) : List<MarketCatalogue>

    @Post("listMarketBook/", processes = [MediaType.APPLICATION_JSON])
    fun listMarketBook(@Body req : ListMarketBookRequest) : List<MarketBook>


}

/******
 *  Betfair Model
 */

//Enum
enum class TimeGranularity {DAYS, HOURS, MINUTES}
enum class PriceData { SP_AVAILABLE, SP_TRADED, EX_BEST_OFFERS, EX_TRADED }
enum class OrderProjection { ALL, EXECUTABLE, EXECUTION_COMPLETE }
enum class MatchProjection { NO_ROLLUP, ROLLED_UP_BY_PRICE, ROLLED_UP_BY_AVG_PRICE }
enum class OrderStatus { PENDING, EXECUTION_COMPLETE, EXECUTABLE, EXPIRED }

//Response Data
data class EventType (val eventType : KeyValue, val marketCount : Int)
data class Competition (val competition : KeyValue, val marketCount : Int)
data class TimeRange (val timeRange : Range<String>, val marketCount : Int)
data class Event (val event : EventDetail, val marketCount : Int)
data class EventDetail(
        override val id : Int, override val name : String,
        val countryCode : String?,
        val timezone : String,
        val openDate : String) : KeyValue(id, name)

data class MarketType (val marketType : String, val marketCount : Int)
data class Country (val countryCode : String, val marketCount : Int)
data class Venue (val venue : String, val marketCount : Int)
data class MarketCatalogue (val marketId : String, val marketName : String, val totalMatched: Number)
data class MarketBook (val marketId : Double, val isMarketDataDelayed : Boolean, val status : String, val betDelay : Int,
                       val bspReconciled : Boolean, val complete : Boolean, val inplay : Boolean, val numberOfWinners : Int,
                       val numberOfRunners : Int, val numberOfActiveRunners : Int, val lastMatchTime : String?, val totalMatched : Double,
                       val totalAvailable : Double, val crossMatching : Boolean, val runnersVoidable : Boolean, val version : String,
                       val runners : List<Runners>
)
data class Runners (val selectionId : Int, val handicap : Int, val status : String, val lastPriceTraded : Double, val totalMatched : Double)


/*****
 * Betfair API Requests
 */
data class MarketFilter (
        val textQuery: String?,
        var eventTypeIds : List<Int> = listOf(),
        var marketCountries : List<String> = listOf(),
        var competitionIds : List<Int> = listOf(),
        var eventIds : List<String> = listOf(),
        var marketIds : List<String> = listOf(),
        var bspOnly: Boolean?,
        var turnInPlayEnabled: Boolean?,
        var inPlayOnly: Boolean?,
        var marketTypeCodes : List<String> = listOf(),
        var marketStartTime : Range<String>?,
        var withOrders: List<OrderStatus> = listOf()
)

data class ListMarketBookRequest(
        val marketIds : List<String>,
        val priceProjection : PriceProjection?,
        val orderProjection : OrderProjection? = OrderProjection.EXECUTABLE,
        val matchProjection : MatchProjection? = MatchProjection.ROLLED_UP_BY_AVG_PRICE,
        val includeOverallPosition: Boolean?,
        val partitionMatchedByStrategyRef: Boolean?,
        val matchedSince : Date?,
)

data class PriceProjection (
        val priceData : List<PriceData> = listOf(),
        val virtualise : Boolean? = false,
        val rolloverStakes : Boolean? = false
)


/*****
 * Utility classes
 */
open class KeyValue(open val id : Int, open val name : String)
open class Range<T>(val from : T, val to : T)




/**
 * Application API Requests
 */
class ExchangeFilterApiRequest {
    val filter: MarketFilter
    var granularity : TimeGranularity? = null
    var maxResults : Int? = null

    constructor(apiRequest: ApiFilterRequest) {
        filter = apiRequest.filter
        if (apiRequest is TimeRangeApiRequest) {
            granularity = apiRequest.granularity
        } else if (apiRequest is MarketCatalogueApiRequest) {
            maxResults = apiRequest.maxResults
        }
    }
}

/**
 * The filter for all requests that use a filter
 */
open class ApiFilterRequest (open val filter: MarketFilter)

/**
 * Market Catalogue Request
 */
class MarketCatalogueApiRequest(
        override val filter: MarketFilter,
        val maxResults: Int = 10) : ApiFilterRequest(filter)

/**
 * Time Range API Request
 */
class TimeRangeApiRequest(
        override val filter : MarketFilter,
        val granularity: TimeGranularity = TimeGranularity.DAYS) : ApiFilterRequest(filter)

/**
 * List Market Book API Request
 */
class ListMarketBookApiRequest(
        val request: ListMarketBookRequest
)