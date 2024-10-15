package org.cglee079.hello.elkthread.external

import org.springframework.stereotype.Component

@Component
class PayValidateAdapter {
    fun validateTx(txId: String): Boolean = true
}