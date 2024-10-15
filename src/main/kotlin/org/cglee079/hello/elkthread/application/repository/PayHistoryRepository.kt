package org.cglee079.hello.elkthread.application.repository

import org.cglee079.hello.elkthread.application.domain.PayHistory
import org.springframework.stereotype.Repository

@Repository
class PayHistoryRepository {
    fun save(payHistory: PayHistory) = PayHistory(1L, payHistory.txId)
}