package com.everden.sfa.betfair

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.swagger.v3.oas.annotations.media.Schema
import javax.inject.Inject

@Controller("/betfair-login")
class LoginController {

    @Inject
    lateinit var authenticate: Authenticate


    @Post(uri = "/login",
            consumes = [MediaType.APPLICATION_JSON],
            produces = [MediaType.APPLICATION_JSON]
    )
    fun login(@Body req: LoginRequest): HttpResponse<LoginResponse?> {

        return try {
            val result = authenticate.authenticate(
                certificatePassword = req.certificatePassword,
                password = req.password,
                appKey = req.appKey,
                username = req.username,
                certificateLocation = req.certificateLocation
            )
            HttpResponse.created(LoginResponse(result))
        } catch (e: IllegalStateException) {
            var error: LoginResponse? = LoginResponse(e.message, "APP_FAILURE")
            HttpResponse.badRequest(error)
        }
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

