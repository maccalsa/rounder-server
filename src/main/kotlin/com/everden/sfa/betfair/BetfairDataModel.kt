package com.everden.sfa.betfair

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime


data class ExchangeApiRequest(val filter: MarketFilter) {
        var granularity = TimeGranularity.DAYS

        constructor(filter: MarketFilter, _granularity: TimeGranularity) : this(filter) {
                granularity = _granularity
        }
}

@Schema(name="Filter", description="Main Search Request")
data class MarketFilter (
        var eventTypeIds : List<Int> = listOf(),
        var marketCountries : List<String> = listOf(),
        var competitionIds : List<Int> = listOf()
)

enum class TimeGranularity {DAYS, HOURS, MINUTES}

data class EventType (val eventType : KeyValue, val marketCount : Int)
data class Competition (val competition : KeyValue, val marketCount : Int)
data class TimeRange (val timeRange : Range<String>, val marketCount : Int)

data class MarketType (val marketType : String, val marketCount : Int)
data class KeyValue(val id : Int, val name : String)
data class Range<T>(val from : T, val to : T)

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







