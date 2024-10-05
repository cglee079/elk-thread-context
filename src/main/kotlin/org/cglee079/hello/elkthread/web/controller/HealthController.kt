package org.cglee079.hello.elkthread.web.controller

import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    val logger = LoggerFactory.getLogger(HealthController::class.java)


    @GetMapping("/api/ping")
    fun ok(): String {
        return "pong"
    }

    @GetMapping("/api/throw")
    fun throwException(): String {
        throw Exception("Throw Test Exception")
    }

    @GetMapping("/api/throw2")
    fun throwException2(): String {
        logger.error("Throw Test Exception2")
        return "Throw Exception!"
    }

    @GetMapping("/api/throw3")
    fun throwException3(): String {
        Sentry.captureException(RuntimeException("Throw Test Exception3"))
        return "Throw Exception!"
    }

    @GetMapping("/api/throw-with-try-catch")
    fun throwExceptionWithTryCatch(txId: String): String {
        try {
            throw RuntimeException("Test Exception")
        } catch (e: Exception) {
            throw RuntimeException("$txId 결제건에서 예외가 발생하였습니다.")
        }
    }
}