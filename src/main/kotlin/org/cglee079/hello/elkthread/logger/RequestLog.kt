package org.cglee079.hello.elkthread.logger

data class RequestLog(
    val request: Request,
    val response: Response,
) {

    data class Request(
        val url: String,
        val queryString: String,
        val method: String,
        val body: String,
        val headers: String,
        val requestAt: String,
    )

    data class Response(
        val status: Int,
        val body: String,
        val bodySize: Int,
        val headers: String,
        val elapseTime: Long,
        val responseAt: String
    )

}