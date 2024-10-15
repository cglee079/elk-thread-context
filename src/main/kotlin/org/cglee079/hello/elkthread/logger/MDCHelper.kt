package org.cglee079.hello.elkthread.logger

import org.slf4j.MDC
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass

object MDCHelper {

    /**
     * Request 요청에 대한 고유한 ID
     */
    val requestId: String
        get() = MDC.get(Key.REQUEST_ID)
            ?: generateUUID().also { setRequestId(it) }

    fun init(requestId: String) {
        clear()
        MDC.put(Key.REQUEST_ID, requestId)
    }

    /**
     * MDC Log Clean 함수
     *
     * MDC Log 에 저장된 데이터를 모두 삭제
     */
    fun clear() {
        MDC.remove(Key.REQUEST_ID)
        MDC.remove(Key.DEBUG)
        MDC.remove(Key.EXCEPTION)
    }

    fun appendDebug(clazz: KClass<*>, message: String) {
        val debugMessage = MDC.get(Key.DEBUG) ?: ""
        val time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

        MDC.put(
            Key.DEBUG,
            "${debugMessage}\n${time} ${clazz.java.canonicalName ?: clazz.java.name} $message"
        )
    }

    fun <T : Any> T.alsoAppendDebug(
        clazz: KClass<*>,
        block: (T) -> String,
    ): T {
        val message = block(this)
        appendDebug(clazz = clazz, message)
        return this
    }


    fun appendException(e: Throwable, message: String = "") {
        val existException = MDC.get(Key.EXCEPTION) ?: ""

        MDC.put(
            Key.EXCEPTION,
            existException
                + "${message}-${e.stackTrace}"
                + "========================================\n\n"
        )
    }

    internal fun getMetadata(): Map<String, Any> =
        mapOf(
            "debug" to (MDC.get(Key.DEBUG) ?: "")
        )

    private fun setRequestId(requestId: String) =
        MDC.put(Key.REQUEST_ID, requestId)

    private fun generateUUID() =
        UUID.randomUUID().toString()

    private object Key {
        const val REQUEST_ID = "requestId"
        const val DEBUG = "debug"
        const val EXCEPTION = "exception"
    }

}