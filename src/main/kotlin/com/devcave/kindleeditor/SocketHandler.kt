package com.devcave.kindleeditor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.CopyOnWriteArraySet


@Component
class SocketHandler : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArraySet<WebSocketSession>()
    private var lastMessage = ""
    private var lastData = "";
    private final val file: File;

    private var charsCount = 0;

    init {
        val dateFormatter = DateTimeFormatter.ISO_DATE
        val localDate = LocalDate.now()
        file = File("post_" + localDate.format(dateFormatter) + ".md")
        try {
            lastMessage = String(Files.readAllBytes(file.toPath()), Charset.defaultCharset())
            val json = jacksonObjectMapper().createObjectNode()
            json.put("text", lastMessage)
            json.put("cursor", lastMessage.length)
            lastData = jacksonObjectMapper().writeValueAsString(json)

            println("Using file: " + file.absolutePath)
        } catch (e: IOException) {
            println("File not exists. Creating new one:" + file.absolutePath)
            try {
                if (file.createNewFile())
                    println("File created.")
            } catch (e1: IOException) {
                println("Cannot create file " + e1.message)
            }

        }

    }

    fun saveToFile() {
        println("Saving to file: " + file.absolutePath)
        Files.write(file.toPath(), lastMessage.toByteArray(Charset.defaultCharset()))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        lastData = message.payload
        lastMessage = jacksonObjectMapper().readTree(message.payload).get("text").asText()

        for (webSocketSession in sessions) {
            try {
                webSocketSession.sendMessage(TextMessage(message.payload))
            } catch (e: Exception) {
                println(e.message)
                sessions.remove(session)

                if (lastMessage.isNotBlank())
                    saveToFile()
            }

        }

        charsCount++;

        if (charsCount > 30) {
            saveToFile()
            charsCount = 0;
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        saveToFile()
        println(session.localAddress.toString() + " disconnected")
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {

        println(session.localAddress.toString() + " connected.")
        sessions.add(session)

        session.sendMessage(TextMessage(lastData))
    }
}