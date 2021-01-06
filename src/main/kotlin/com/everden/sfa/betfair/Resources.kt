package com.everden.sfa.betfair

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import kotlin.jvm.Throws

object Resources {
    val LOCATION = String.format(
            "%s/.rounder-server/token.properties",
            System.getenv("HOME")
    )

    fun init() {
        val file = File(LOCATION)
        val parent = file.parentFile
        createDirs(parent)
        try {
            createFiles(file)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        System.setProperty("micronaut.config.files", LOCATION)

    }

    @Throws(IOException::class)
    private fun createFiles(file: File) {
        check(!(!file.exists() && !file.createNewFile())) { "Cannot create file " + file.absolutePath }
    }

    private fun createDirs(parent: File) {
        check(!(!parent.exists() && !parent.mkdirs())) { "Cannot create folder " + parent.absolutePath }
    }

    fun store(token: String) {
        try {
            FileOutputStream(File(LOCATION)).use { os ->
                val content = "rounder-server.credentials.token=$token\n"
                os.write(content.toByteArray(StandardCharsets.UTF_8))
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}