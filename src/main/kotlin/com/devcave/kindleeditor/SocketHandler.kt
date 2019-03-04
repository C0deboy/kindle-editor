package com.devcave.kindleeditor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArraySet


@Component
class SocketHandler : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArraySet<WebSocketSession>()
    private var lastData = Backup.init()

    private var charsCount = 0;

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        lastData = message.payload

        for (webSocketSession in sessions) {
            try {
                webSocketSession.sendMessage(TextMessage(message.payload))
            } catch (e: Exception) {
                println(e.message)
                sessions.remove(session)

                val text = jacksonObjectMapper().readTree(message.payload).get("text").asText()

                if (text.isNotBlank())
                    Backup.saveToCurrentFile(text)
            }

        }

        charsCount++;

        if (charsCount > 30) {
            val text = jacksonObjectMapper().readTree(message.payload).get("text").asText()
            Backup.saveToCurrentFile(text)
            charsCount = 0;
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        val text = jacksonObjectMapper().readTree(lastData).get("text").asText()
        Backup.saveToCurrentFile(text)
        println("${session.localAddress.toString()} disconnected")
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {

        println("${session.localAddress.toString()} connected.")
        sessions.add(session)

        session.sendMessage(TextMessage(lastData))
    }
}