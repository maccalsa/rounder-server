package com.everden.sfa.betfair


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.ConfigurationInject
import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Value
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.URL
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext


@Singleton
class Authenticate {

    @Inject
    lateinit var objectMapper: ObjectMapper

    val DEFAULT_LOCATION = String.format(
            "%s/.rounder-server/client-2048.p12",
            System.getenv("HOME")
    )

    @Value("\${rounder-server.credentials.token:}")
    var sessionKey : String = ""

    @Value("\${betfair.p12certificate.password:}")
    var certificatePassword : String = ""

    @Value("\${betfair.password:}")
    var password : String = ""

    @Value("\${betfair.username:}")
    var username : String = ""

    @Value("\${betfair.appkey:}")
    var appKey : String = ""

    fun authenticate() {
        sessionKey = authenticate(
            certificatePassword = certificatePassword,
            password = password,
            appKey = appKey,
            username = username,
            certificateLocation = DEFAULT_LOCATION
         )
    }

    fun authenticate(certificateLocation: String?,
                     certificatePassword: String,
                     username: String,
                     password: String,
                     appKey: String): String {

        var keyStream: FileInputStream? = null
        var loginResponse : LoginResponse?
        try {
            var file = File(certificateLocation.let {it} ?: DEFAULT_LOCATION)

            val clientStore = KeyStore.getInstance("PKCS12")
            keyStream = FileInputStream(file)
            clientStore.load(keyStream, certificatePassword.toCharArray())

            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            kmf.init(clientStore, certificatePassword.toCharArray())
            val kms = kmf.keyManagers

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(kms, null, SecureRandom())

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
            val url = URL("https://identitysso-cert.betfair.com/api/certlogin")

            val postData ="username=${username}&password=${password}"

            val request = url.openConnection() as HttpsURLConnection
            request.requestMethod = "POST"
            request.setRequestProperty("X-Application", appKey)
            request.doOutput = true
            val writer = DataOutputStream(request.outputStream)
            writer.writeBytes(postData)
            writer.flush()
            writer.close()

            val inputAsString = request.inputStream.readBytes().toString(Charset.defaultCharset())
            request.inputStream.close()

            val objectMapper = ObjectMapper().registerModule(KotlinModule())
            loginResponse = objectMapper.readValue<LoginResponse>(inputAsString)
            Resources.store(loginResponse.sessionToken as String)
            return loginResponse?.sessionToken as String
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        } finally {
            try {
                keyStream!!.close()
            } catch (ignore: Exception) {
            }

        }


        return ""

    }

}

