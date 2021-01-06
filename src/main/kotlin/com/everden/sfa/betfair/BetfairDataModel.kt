package com.everden.sfa.betfair

import io.swagger.v3.oas.annotations.media.Schema

data class MarketFilter (val eventTypeIds : List<Int>, val marketCountries : List<String>)
data class MarketType (val marketType : String, val marketCount : Int)

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







