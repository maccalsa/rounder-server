package com.everden.sfa.betfair

import io.swagger.v3.oas.annotations.media.Schema

// Query request to Betfair
data class ExchangeApiRequest(val filter: MarketFilter) {
        var granularity = TimeGranularity.DAYS

        constructor(filter: MarketFilter, _granularity: TimeGranularity) : this(filter) {
                granularity = _granularity
        }
}

// The filter for thr query request to befair
@Schema(name="Filter", description="Main Search Request")
data class MarketFilter (
        var eventTypeIds : List<Int> = listOf(),
        var marketCountries : List<String> = listOf(),
        var competitionIds : List<Int> = listOf()
)

// Enum
enum class TimeGranularity {DAYS, HOURS, MINUTES}


// Return types from betfair
data class EventType (val eventType : KeyValue, val marketCount : Int)
data class Competition (val competition : KeyValue, val marketCount : Int)
data class TimeRange (val timeRange : Range<String>, val marketCount : Int)
data class Event (val event : EventDetail, val marketCount : Int)
data class EventDetail(
                 override val id : Int, override val name : String,
                 val countryCode : String,
                 val timezone : String,
                 val openDate : String) : KeyValue(id, name)

data class MarketType (val marketType : String, val marketCount : Int)


// Utility classes
open class KeyValue(open val id : Int, open val name : String)
open class Range<T>(val from : T, val to : T)




@Schema(name="ApiRequest", description="Main Search Request")
data class ApiRequest (
        @Schema(description="the application key")
        val appKey: String,
        @Schema(description="the market filter criteria")
        val filter: MarketFilter
) {
        var granularity = TimeGranularity.DAYS

        constructor(_appKey: String, _filter: MarketFilter, _granularity: TimeGranularity) : this(_appKey, _filter) {
                granularity = _granularity
        }
}

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







