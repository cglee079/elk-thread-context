package org.cglee079.hello.elkthread.external

import org.springframework.stereotype.Component

@Component
class EmailSendAdapter {
    fun send(title: String, content: String): Boolean = true
}