package raging.goblin.gitlab.ci.dashboard.mapping

import org.gitlab4j.api.GitLabApiException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

fun toResponseStatusException(exception: GitLabApiException): ResponseStatusException {
    val status = HttpStatus.resolve(exception.httpStatus) ?: HttpStatus.INTERNAL_SERVER_ERROR
    return ResponseStatusException(status, exception.message, exception)
}

inline fun <T> runGitLabCall(block: () -> T): T {
    return try {
        block()
    } catch (exception: GitLabApiException) {
        throw toResponseStatusException(exception)
    }
}
