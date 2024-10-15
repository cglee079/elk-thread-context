package org.cglee079.hello.elkthread.logger

import io.sentry.Sentry
import org.cglee079.hello.elkthread.util.toISO
import org.cglee079.hello.elkthread.values.constant.SentryExtra
import org.cglee079.hello.elkthread.values.constant.SentryTag
import org.slf4j.MDC
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.KClass

object MDCHelper {

    /**
     * Request 요청에 대한 고유한 ID
     */
    val requestId: String
        get() = MDC.get(Key.REQUEST_ID)
            ?: generateUUID().also { setRequestId(it) }

    val contextId: String
        get() = MDC.get(Key.CONTEXT_ID)
            ?: generateUUID().also { setContextId(it) }

    fun init(requestId: String, contextId: String) {
        clear()
        MDC.put(Key.REQUEST_ID, requestId)
        MDC.put(Key.CONTEXT_ID, contextId)
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

    /**
     * 새로운 MDC Log Context를 만듬
     *
     * 새로운 Thread Local을 만들기 위해, 쓰레드를 동기적으로 사용함
     */
    fun <T> onNewContext(title: String = "", function: () -> T): T {
        val parentRequestId = requestId // 부모 쓰레드의 RequestId
        val parentContextId = contextId // 부모 쓰레드의 ContextId

        return Executors.newSingleThreadExecutor().submit<T> {
            // 여기서 부터 새로운 MDC Context
            // 여기서 부터 새로운 Sentry Context
            val childContextId = generateUUID()
            init(parentRequestId, childContextId)

            Sentry.setTag(SentryTag.REQUEST_ID, parentRequestId)
            Sentry.setTag(SentryTag.CONTEXT_ID, childContextId)
            Sentry.setExtra(SentryExtra.KIBANA_URL, createKibanaUrl(contextId))

            appendDebug(this::class, ">>>>> New Context Start, From : '$parentContextId' <<<<<")

            val requestAt = LocalDateTime.now()

            return@submit try {
                function.invoke()
            } catch (e: Exception) {
                Sentry.captureException(e)
                throw e
            } finally {
                val responseAt = LocalDateTime.now()

                ElkLogger.info(
                    message = "NEW_CONTEXT",
                    requestLog = RequestLog(
                        requestId = requestId,
                        contextId = childContextId,
                        request = RequestLog.Request(
                            url = "New Context From ContextId: '$parentContextId'" + if (title.isNotBlank()) " (${title})" else "",
                            requestAt = requestAt.toISO()
                        ),
                        response = RequestLog.Response(
                            responseAt = responseAt.toISO()
                        ),
                        metadata = MDCHelper.getMetadata()
                    )
                )


                Sentry.removeTag(SentryTag.REQUEST_ID)
                Sentry.removeTag(SentryTag.CONTEXT_ID)
                MDCHelper.clear()
            }
        }.get()
    }

    fun createKibanaUrl(contextId: String) =
        "http://kibana.local/app/kibana#/discover?_g=(time:(from:now-7d)&_a=(index:'103c2e10-77da-11ef-a17a-07241150b3ca',query:(language:kqeury,query:'contextId:%22${contextId}%22'))"


    internal fun getMetadata(): Map<String, Any> =
        mapOf(
            "debug" to (MDC.get(Key.DEBUG) ?: "")
        )

    private fun setRequestId(requestId: String) =
        MDC.put(Key.REQUEST_ID, requestId)

    private fun setContextId(contextId: String) =
        MDC.put(Key.CONTEXT_ID, contextId)


    private fun generateUUID() =
        UUID.randomUUID().toString()

    private object Key {
        const val REQUEST_ID = "requestId"
        const val CONTEXT_ID = "contextId"
        const val DEBUG = "debug"
        const val EXCEPTION = "exception"
    }

}