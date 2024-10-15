package org.cglee079.hello.elkthread.application.service

import org.cglee079.hello.elkthread.external.EmailSendAdapter
import org.cglee079.hello.elkthread.logger.MDCHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AdminNotifyService(
    private val emailSendAdapter: EmailSendAdapter
) {

    @Async
    fun sendAsync(message: String) {
        MDCHelper.onNewContext("관리자 알림 전송") {
            emailSendAdapter.send("관리자 알림", message)
        }
    }
}