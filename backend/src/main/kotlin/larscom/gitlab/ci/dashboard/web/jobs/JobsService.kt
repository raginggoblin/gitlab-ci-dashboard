package larscom.gitlab.ci.dashboard.web.jobs

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import larscom.gitlab.ci.dashboard.api.model.Job
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.gitlab.GitLabClient
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel

@Service
class JobsService(private val gitLabClient: GitLabClient) {
    private val logger = LoggerFactory.getLogger(JobsService::class.java)

    @Cacheable(
        cacheNames = [CacheNames.JOBS],
        key = "#projectId + ':' + #pipelineId + ':' + #scope.replaceAll('\\\\s+', '').toLowerCase()",
    )
    fun getJobs(projectId: Int, pipelineId: Int, scope: String): List<Job> {
        logger.debug("Jobs service call: get jobs projectId={} pipelineId={} scope={}", projectId, pipelineId, scope)
        val scopes = parseScopes(scope)
        return getJobs(projectId, pipelineId, scopes)
    }

    @Cacheable(cacheNames = [CacheNames.JOBS], key = "#projectId + ':' + #pipelineId + ':failed'")
    fun getFailedJobs(projectId: Int, pipelineId: Int): List<Job> {
        logger.debug("Jobs service call: get failed jobs projectId={} pipelineId={}", projectId, pipelineId)
        return getJobs(projectId, pipelineId, listOf("failed"))
    }

    private fun getJobs(projectId: Int, pipelineId: Int, scopes: List<String>): List<Job> {
        return runGitLabCall {
            val jobs = gitLabClient.getJobsForPipeline(projectId, pipelineId, scopes)

            jobs
                .distinctBy { it.id }
                .map { it.toApiModel() }
                .sortedBy { it.createdAt }
        }
    }

    private fun parseScopes(scope: String): List<String> {
        if (scope.isBlank()) {
            return emptyList()
        }

        val supportedScopes = setOf(
            "created",
            "pending",
            "running",
            "failed",
            "success",
            "canceled",
            "skipped",
            "manual",
            "scheduled",
            "waiting_for_resource",
            "preparing",
            "canceling",
        )

        return scope
            .split(',')
            .map { token -> token.trim().lowercase() }
            .filter { token -> token.isNotBlank() }
            .map { token ->
                if (supportedScopes.contains(token)) {
                    token
                } else {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported job scope: $token")
                }
            }
            .distinct()
    }
}
