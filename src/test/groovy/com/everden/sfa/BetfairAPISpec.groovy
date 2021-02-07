package com.everden.sfa

import com.everden.sfa.betfair.Authenticate
import com.everden.sfa.betfair.api.LoginRequest
import com.everden.sfa.betfair.api.LoginResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

import static io.micronaut.http.HttpRequest.POST

@MicronautTest
class BetfairAPISpec extends Specification {


    @Inject @Client("/")
    RxHttpClient client

    @Inject
    Authenticate authenticate

    void "Test Mocked SUCCESS Login"() {
        given:
        LoginRequest request = new LoginRequest(
            "CERT-LOCATION",
            "CERT-PASSWORD",
            "USERNAME",
            "PASSWORD",
            "APP-KEY")


        when:
        authenticate.authenticate(
            request.certificateLocation,
            request.certificatePassword,
            request.username,
            request.password,
            request.appKey) >> new LoginResponse("12345", "SUCCESS")


         HttpResponse result = client.toBlocking().exchange(
            POST('betfair/login', request), LoginResponse)

        then:
        result.status == HttpStatus.CREATED
        result.body.isPresent() == true
        def body = result.body.get()
        body.loginStatus == "SUCCESS"
        body.sessionToken == "12345"
    }

    void "Test Mocked NOT SUCCESS Login"() {
        given:
        LoginRequest request = new LoginRequest(
                "CERT-LOCATION",
                "CERT-PASSWORD",
                "USERNAME",
                "PASSWORD",
                "APP-KEY")


        when:
        authenticate.authenticate(
                request.certificateLocation,
                request.certificatePassword,
                request.username,
                request.password,
                request.appKey) >> new LoginResponse(null, "SOMETHING")


        HttpResponse result = client.toBlocking().exchange(
                POST('betfair/login', request), LoginResponse)

        then:
        result.status == HttpStatus.CREATED
        result.body.isPresent() == true
        def body = result.body.get()
        body.loginStatus == "SOMETHING"
        body.sessionToken == null
    }


    @MockBean(Authenticate)
    Authenticate authenticate() {
        Mock(Authenticate)
    }

}
