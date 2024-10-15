package org.cglee079.hello.elkthread.external

import org.springframework.stereotype.Component

@Component
class BankAdapter {
    fun use(txId: String): Boolean = true
}