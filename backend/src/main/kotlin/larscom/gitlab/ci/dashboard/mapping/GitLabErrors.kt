package larscom.gitlab.ci.dashboard.mapping

import org.springframework.http.HttpStatusCode
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.server.ResponseStatusException

fun toResponseStatusException(exception: RestClientResponseException): ResponseStatusException {
    val reason = exception.responseBodyAsString.takeIf { it.isNotBlank() } ?: exception.statusText
    return ResponseStatusException(HttpStatusCode.valueOf(exception.statusCode.value()), reason, exception)
}

inline fun <T> runGitLabCall(block: () -> T): T {
    return try {
        block()
    } catch (exception: RestClientResponseException) {
        throw toResponseStatusException(exception)
    }
}
