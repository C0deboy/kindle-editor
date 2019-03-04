package com.devcave.kindleeditor

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.HtmlUtils

@RestController
@CrossOrigin
class EditorRestController {

    @PostMapping("/backup")
    fun sendContent(@RequestBody text: String) {
        Backup.makeBackup(text)
    }

}