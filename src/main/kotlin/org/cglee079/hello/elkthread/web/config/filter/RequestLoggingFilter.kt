package org.cglee079.hello.elkthread.web.config.filter

import io.sentry.Sentry
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.cglee079.hello.elkthread.logger.ElkLogger
import org.cglee079.hello.elkthread.logger.MDCHelper
import org.cglee079.hello.elkthread.logger.RequestLog
import org.cglee079.hello.elkthread.values.constant.SentryExtra
import org.cglee079.hello.elkthread.values.constant.SentryTag
import org.cglee079.hello.elkthread.util.toISO
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MILLIS
import java.util.UUID

class RequestLoggingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        servletRequest: HttpServletRequest,
        servletResponse: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val requestId = UUID.randomUUID().toString()
        MDCHelper.init(requestId)
        Sentry.setTag(SentryTag.REQUEST_ID, requestId)
        Sentry.setExtra(SentryExtra.KIBANA_URL, createKibanaUrl(requestId))

        val request = ContentCachingRequestWrapper(servletRequest)
        val response = ContentCachingResponseWrapper(servletResponse)

        val requestAt = LocalDateTime.now()

        try {
            filterChain.doFilter(request, response)
        } finally {
            val responseAt = LocalDateTime.now()

            ElkLogger.info(
                "Request Log",
                RequestLog(
                    requestId = requestId,
                    request = createRequestLog(request, requestAt),
                    response = createResponseLog(response, requestAt, responseAt),
                    metadata = MDCHelper.getMetadata()
                )
            )

            response.copyBodyToResponse()
        }
    }

    private fun createRequestLog(request: ContentCachingRequestWrapper, requestAt: LocalDateTime): RequestLog.Request {
        val headers = request.headerNames.toList()
            .associateWith { request.getHeaders(it).toList().toString() }
            .toMap()

        return RequestLog.Request(
            url = request.requestURI.toString(),
            queryString = request.queryString ?: "",
            method = request.method,
            body = String(request.contentAsByteArray),
            headers = headers.toString(),
            requestAt = requestAt.toISO()
        )
    }

    private fun createResponseLog(
        response: ContentCachingResponseWrapper,
        requestAt: LocalDateTime,
        responseAt: LocalDateTime
    ): RequestLog.Response {
        val headers = response.headerNames.toList()
            .associateWith { response.getHeaders(it).toList().toString() }
            .toMap()

        return RequestLog.Response(
            status = response.status,
            body = String(response.contentAsByteArray),
            elapseTime = MILLIS.between(requestAt, responseAt),
            headers = headers.toString(),
            bodySize = response.contentSize,
            responseAt = responseAt.toISO()
        )
    }

    private fun createKibanaUrl(requestId: String): String =
        "http://kibana.local/app/kibana#/discover?_g=(time:(from:now-7d)&_a=(index:'103c2e10-77da-11ef-a17a-07241150b3ca',query:(language:kqeury,query:'requestId:%22${requestId}%22'))"
}
