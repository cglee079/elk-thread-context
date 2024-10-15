package org.cglee079.hello.elkthread.application.service

import org.cglee079.hello.elkthread.application.domain.PayHistory
import org.cglee079.hello.elkthread.external.BankAdapter
import org.cglee079.hello.elkthread.external.PayValidateAdapter
import org.cglee079.hello.elkthread.logger.MDCHelper.alsoAppendDebug
import org.cglee079.hello.elkthread.application.repository.PayHistoryRepository
import org.springframework.stereotype.Service

@Service
class PayService(
    private val payHistoryRepository: PayHistoryRepository,
    private val payValidateAdapter: PayValidateAdapter,
    private val bankAdapter: BankAdapter,
) {

    fun pay(txId: String): PayResult {

        payHistoryRepository.save(PayHistory(null, txId))
            .alsoAppendDebug(this::class) {
                "PayHistory 생성, id : ${it.id}"
            }

        payValidateAdapter.validateTx(txId)
            .alsoAppendDebug(this::class) {
                "결제 트랜잭션 검증 결과 : ${it}"
            }

        bankAdapter.use(txId)
            .alsoAppendDebug(this::class) {
                "은행 사용 요청 결과 : $it"
            }

        return PayResult(true)
    }
}

data class PayResult(val isSuccess: Boolean)