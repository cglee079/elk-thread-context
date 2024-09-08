package org.cglee079.hello.elkthread.logger

import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ElkLogger {

    private val logger: Logger = LoggerFactory.getLogger("ELK_LOGGER")

    fun info(message: String = "", requestLog: RequestLog) {
        logger.info(message, StructuredArguments.fields(requestLog))
    }

}
