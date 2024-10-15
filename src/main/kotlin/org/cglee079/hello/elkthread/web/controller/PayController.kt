package org.cglee079.hello.elkthread.web.controller

import org.cglee079.hello.elkthread.application.service.PayResult
import org.cglee079.hello.elkthread.application.service.PayService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PayController(
    private val payService: PayService
) {

    @GetMapping("/api/pay")
    fun pay(txId: String): PayResponse =
        payService.pay(txId).let { PayResponse.of(it) }
}

data class PayResponse(
    val isSuccess: Boolean
) {

    companion object {
        fun of(payResult: PayResult): PayResponse = PayResponse(payResult.isSuccess)
    }
}