package raging.goblin.gitlab.ci.dashboard.web.jobs

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.models.Constants
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import raging.goblin.gitlab.ci.dashboard.api.model.Job
import raging.goblin.gitlab.ci.dashboard.mapping.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.mapping.toApiModel

@Service
class JobsService(private val gitLabApi: GitLabApi) {
    private val logger = LoggerFactory.getLogger(JobsService::class.java)

    fun getJobs(projectId: Int, pipelineId: Int, scope: String): List<Job> {
        logger.debug("Jobs service call: get jobs projectId={} pipelineId={} scope={}", projectId, pipelineId, scope)
        val scopes = parseScopes(scope)
        return getJobs(projectId, pipelineId, scopes)
    }

    fun getFailedJobs(projectId: Int, pipelineId: Int): List<Job> {
        logger.debug("Jobs service call: get failed jobs projectId={} pipelineId={}", projectId, pipelineId)
        return getJobs(projectId, pipelineId, listOf(Constants.JobScope.FAILED))
    }

    private fun getJobs(projectId: Int, pipelineId: Int, scopes: List<Constants.JobScope>): List<Job> {
        return runGitLabCall {
            val jobs = if (scopes.isEmpty()) {
                gitLabApi.jobApi.getJobsForPipeline(projectId, pipelineId.toLong())
            } else {
                scopes.flatMap { scope ->
                    gitLabApi.jobApi.getJobsForPipeline(projectId, pipelineId.toLong(), scope)
                }
            }

            jobs
                .distinctBy { it.id }
                .map { it.toApiModel() }
                .sortedBy { it.createdAt }
        }
    }

    private fun parseScopes(scope: String): List<Constants.JobScope> {
        if (scope.isBlank()) {
            return emptyList()
        }

        return scope
            .split(',')
            .map { token -> token.trim() }
            .filter { token -> token.isNotBlank() }
            .map { token ->
                runCatching { Constants.JobScope.forValue(token) }.getOrElse {
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported job scope: $token")
                }
            }
            .distinct()
    }
}
