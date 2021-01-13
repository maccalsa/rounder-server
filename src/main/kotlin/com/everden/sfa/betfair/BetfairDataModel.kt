package com.everden.sfa.betfair

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

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
 * Parent application class that all requests inherit
 */
open class ApiRequestKey ( open val appKey: String)

/**
 * The filter for all requests that use a filter
 */
open class ApiFilterRequest (override val appKey: String, open val filter: MarketFilter) : ApiRequestKey(appKey)

/**
 * Market Catalogue Request
 */
class MarketCatalogueApiRequest(
        override val appKey: String,
        override val filter: MarketFilter,
        val maxResults: Int = 10) : ApiFilterRequest(appKey, filter)

/**
 * Time Range API Request
 */
class TimeRangeApiRequest(
        override val appKey: String,
        override val filter : MarketFilter,
        val granularity: TimeGranularity = TimeGranularity.DAYS) : ApiFilterRequest(appKey, filter)

/**
 * List Market Book API Request
 */
class ListMarketBookApiRequest(
        override val appKey: String,
        val request: ListMarketBookRequest
)  : ApiRequestKey(appKey)

@Schema(name="LoginResponse", description="The TEST Login Request")
class LoginResponse (
        @Schema(description="the session token")
        var sessionToken: String? = null,

        @Schema(description="the response")
        var loginStatus: String? = null
)

@Schema(name="LoginRequest", description="The TEST Login Request")
class LoginRequest (
        @Schema(description="the p12 file location")
        var certificateLocation : String?,

        @Schema(description="the p12 file password")
        val certificatePassword : String,

        @Schema(description="the account user name")
        val username : String,

        @Schema(description="the account password")
        val password : String,

        @Schema(description="the application key")
        val appKey : String)




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
@Schema(name="Market Filter", description="Main Search Request")
data class MarketFilter (
        @Schema(name="textQuery", description = "Restrict markets by any text associated with the Event name. You can include a\n" +
                "          wildcard (*) character as long as it is not the first character. Please note - \n" +
                "          the textQuery field doesn't evaluate market or selection names.", example = "comeone", defaultValue = "come one2")
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

@Schema(name="ListMarketBookRequest", description="List Marke tbook data")
data class ListMarketBookRequest(
        @Schema(description="One or more market ids. The number of markets returned depends on the amount of data you request via the price projection")
        val marketIds : List<String>,

        @Schema(description="One or more market ids. The number of markets returned depends on the amount of data you request via the price projection")
        val priceProjection : PriceProjection?,

        @Schema(description="The projection of price data you want to receive in the response.")
        val orderProjection : OrderProjection? = OrderProjection.EXECUTABLE,

        @Schema(description="The projection of price data you want to receive in the response.")
        val matchProjection : MatchProjection? = MatchProjection.ROLLED_UP_BY_AVG_PRICE,

        @Schema(description="If you ask for orders, returns matches for each selection. Defaults to true if unspecified.")
        val includeOverallPosition: Boolean?,

        @Schema(description="If you ask for orders, returns the breakdown of matches by strategy for each selection. Defaults to false if unspecified.")
        val partitionMatchedByStrategyRef: Boolean?,

        val matchedSince : Date?,
)

data class PriceProjection (
        @Schema(description="The basic price data you want to receive in the response.")
        val priceData : List<PriceData> = listOf(),
        //exBestOffersOverrides? : any;
        val virtualise : Boolean? = false,
        val rolloverStakes : Boolean? = false
)


/*****
 * Utility classes
 */
open class KeyValue(open val id : Int, open val name : String)
open class Range<T>(val from : T, val to : T)