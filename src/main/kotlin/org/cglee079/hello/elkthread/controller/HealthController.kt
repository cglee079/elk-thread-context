package org.cglee079.hello.elkthread.controller

import io.sentry.Sentry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/api/ping")
    fun ok(): String {
        return "pong"
    }

    @GetMapping("/api/throw")
    fun throwException(): String {
        throw Exception("Throw Test Exception")
    }
}