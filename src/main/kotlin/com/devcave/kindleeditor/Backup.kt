package com.devcave.kindleeditor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Backup {
    private val file = File("post_current.md")
    private var lastContent = ""

    fun init(): String {

        try {
            lastContent = String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())

            val json = jacksonObjectMapper().createObjectNode()
            json.put("text", lastContent)
            val cursor = json.putObject("cursor")
            cursor.put("start", lastContent.length)
            cursor.put("end", lastContent.length)

            println("Using file: ${file.absolutePath}")

            return jacksonObjectMapper().writeValueAsString(json)

        } catch (e: IOException) {
            println("File not exists. Creating new one: ${file.absolutePath}")
            try {
                if (file.createNewFile())
                    println("File created.")

            } catch (e1: IOException) {
                println("Cannot create file ${e1.message}")
            }
        }

        return lastContent
    }

    fun saveToCurrentFile(text: String) {
        println("Saving to file: ${file.absolutePath}")
        Files.write(file.toPath(), text.toByteArray(Charset.defaultCharset()))
    }

    fun makeBackup(text: String) {
        val dateFormatter = DateTimeFormatter.ISO_DATE
        val localDate = LocalDate.now()
        val backupFile = File("post_${localDate.format(dateFormatter)}.md")
        println("Making backup to file: ${backupFile.absolutePath}")
        Files.write(backupFile.toPath(), text.toByteArray(Charset.defaultCharset()))
    }
}