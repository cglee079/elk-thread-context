package org.cglee079.hello.elkthread.job

import org.cglee079.hello.elkthread.logger.MDCHelper
import org.cglee079.hello.elkthread.application.repository.ReserveJobRepository
import org.cglee079.hello.elkthread.application.service.AdminNotifyService
import org.cglee079.hello.elkthread.application.service.PayService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ReservePayJob(
    private val reserveJobRepository: ReserveJobRepository,
    private val adminNotifyService: AdminNotifyService,
    private val payService: PayService,
) {

    fun run(now: LocalDateTime = LocalDateTime.now()) {

        adminNotifyService.sendAsync("$now, 예약 결제 배치 시작")

        reserveJobRepository.findByBetweenAt(now.minusMinutes(10L), now)
            .forEach {
                MDCHelper.onNewContext("결제 진행, txId: ${it.txId}") {
                    payService.pay(it.txId)
                }
            }

        adminNotifyService.sendAsync("$now, 예약 결제 배치 종료")
    }

}