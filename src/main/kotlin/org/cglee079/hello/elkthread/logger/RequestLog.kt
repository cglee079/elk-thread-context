package org.cglee079.hello.elkthread.logger

data class RequestLog(
    val requestId: String,
    val contextId: String,
    val request: Request = Request(),
    val response: Response = Response(),
    val metadata: Map<String, Any>
) {

    data class Request(
        val url: String = "",
        val queryString: String = "",
        val method: String = "",
        val body: String = "",
        val headers: String = "",
        val requestAt: String = "",
    )

    data class Response(
        val status: Int = 200,
        val body: String = "",
        val bodySize: Int = 0,
        val headers: String = "",
        val elapseTime: Long = 0,
        val responseAt: String = ""
    )

}