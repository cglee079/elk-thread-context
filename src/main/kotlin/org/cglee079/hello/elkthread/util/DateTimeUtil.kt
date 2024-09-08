package org.cglee079.hello.elkthread.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun LocalDateTime.toISO(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)