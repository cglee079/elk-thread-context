package org.cglee079.hello.elkthread.application.repository

import org.cglee079.hello.elkthread.application.domain.ResolvePay
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
class ReservePayRepository {

    fun findByBetweenAt(startAt: LocalDateTime, endAt: LocalDateTime): List<ResolvePay> =
        (0..1000L).map { ResolvePay(it, UUID.randomUUID().toString()) }
}