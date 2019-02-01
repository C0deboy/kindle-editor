package com.devcave.kindleeditor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class KindleEditorApplication

fun main(args: Array<String>) {
	runApplication<KindleEditorApplication>(*args)
}

