package com.everden.sfa.betfair

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import java.lang.IllegalStateException
import javax.inject.Inject

@Controller("/betfair")
class BetfairController {

    @Inject
    lateinit var authenticate : Authenticate

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

}
