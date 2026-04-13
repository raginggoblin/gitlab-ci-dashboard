package larscom.gitlab.ci.dashboard.web.pipelines

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import larscom.gitlab.ci.dashboard.api.model.Pipeline
import larscom.gitlab.ci.dashboard.api.model.Source
import larscom.gitlab.ci.dashboard.api.model.StartPipelineRequest
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.config.DashboardProperties
import larscom.gitlab.ci.dashboard.gitlab.GitLabClient
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PipelinesService(
    private val gitLabClient: GitLabClient,
    private val properties: DashboardProperties,
) {
    private val logger = LoggerFactory.getLogger(PipelinesService::class.java)

    @CacheEvict(cacheNames = [CacheNames.PIPELINES, CacheNames.JOBS], allEntries = true)
    fun cancelPipeline(projectId: Int, pipelineId: Int): Pipeline {
        logger.debug("Pipelines service call: cancel pipeline projectId={} pipelineId={}", projectId, pipelineId)
        verifyWritable("can't cancel pipeline when in 'read only' mode")

        return runGitLabCall {
            gitLabClient.cancelPipeline(projectId, pipelineId).toApiModel()
        }
    }

    @Cacheable(
        cacheNames = [CacheNames.PIPELINES],
        key = "#projectId + ':' + (#source == null ? 'all' : #source.name())",
    )
    fun getPipelines(projectId: Int, source: Source?): List<Pipeline> {
        logger.debug("Pipelines service call: get pipelines projectId={} source={}", projectId, source)

        val updatedAfter = Instant.now().minus(properties.pipelineHistoryDays, ChronoUnit.DAYS)

        return runGitLabCall {
            val pipelines = gitLabClient.getPipelines(projectId, updatedAfter).map { it.toApiModel() }
            if (source == null) {
                pipelines
            } else {
                pipelines.filter { it.source == source }
            }
        }
    }

    @CacheEvict(cacheNames = [CacheNames.PIPELINES, CacheNames.JOBS], allEntries = true)
    fun retryPipeline(projectId: Int, pipelineId: Int): Pipeline {
        logger.debug("Pipelines service call: retry pipeline projectId={} pipelineId={}", projectId, pipelineId)
        verifyWritable("can't retry pipeline when in 'read only' mode")

        return runGitLabCall {
            gitLabClient.retryPipeline(projectId, pipelineId).toApiModel()
        }
    }

    @CacheEvict(cacheNames = [CacheNames.PIPELINES, CacheNames.JOBS], allEntries = true)
    fun startPipeline(request: StartPipelineRequest): Pipeline {
        logger.debug("Pipelines service call: start pipeline projectId={} branch={}", request.projectId, request.branch)
        verifyWritable("can't start a new pipeline when in 'read only' mode")

        return runGitLabCall {
            gitLabClient
                .createPipeline(request.projectId, request.branch, request.envVars ?: emptyMap())
                .toApiModel()
        }
    }

    @Cacheable(cacheNames = [CacheNames.PIPELINES], key = "#projectId + ':latest:' + #branch")
    fun getLatestPipeline(projectId: Int, branch: String): Pipeline? {
        return runGitLabCall {
            gitLabClient.getLatestPipeline(projectId, branch)?.toApiModel()
        }
    }

    private fun verifyWritable(message: String) {
        if (properties.readOnly) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)
        }
    }
}
