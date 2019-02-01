package com.devcave.kindleeditor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

@RestController
@CrossOrigin
public class EditorRestController {

    private String content = "ready";

    @CrossOrigin
    @GetMapping(value = "/content", produces = {"text/plain"})
    public @ResponseBody String getContent() throws Exception {
        return content;
    }

    @PostMapping("/content")
    public void sendContent(@RequestBody String text) throws Exception {
        content = text;
    }

}