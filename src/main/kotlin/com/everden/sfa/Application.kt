package com.everden.sfa

import com.everden.sfa.betfair.Resources
import io.micronaut.runtime.Micronaut.*
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*

@OpenAPIDefinition(
    info = Info(
            title = "rounder-server",
            version = "0.1"
    )
)
object Api {
}
fun main(args: Array<String>) {
	Resources.init()
	build()
	    .args(*args)
		.packages("com.everden.sfa")
		.start()
}

