package org.cglee079.hello.elkthread.web.controller

import org.cglee079.hello.elkthread.job.ReservePayJob
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class JobController(
    private val reservePayJob: ReservePayJob
) {

    @GetMapping("/api/jobs/reserve-pay")
    fun runReservePay() {
        reservePayJob.run(LocalDateTime.now())
    }
}