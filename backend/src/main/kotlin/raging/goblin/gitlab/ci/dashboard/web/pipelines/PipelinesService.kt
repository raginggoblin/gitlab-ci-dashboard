package raging.goblin.gitlab.ci.dashboard.web.pipelines

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.PipelineFilter
import org.gitlab4j.models.Constants
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import raging.goblin.gitlab.ci.dashboard.api.model.Pipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Source
import raging.goblin.gitlab.ci.dashboard.api.model.StartPipelineRequest
import raging.goblin.gitlab.ci.dashboard.config.DashboardProperties
import raging.goblin.gitlab.ci.dashboard.mapping.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.mapping.toApiModel
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date

@Service
class PipelinesService(
    private val gitLabApi: GitLabApi,
    private val properties: DashboardProperties,
) {
    private val logger = LoggerFactory.getLogger(PipelinesService::class.java)

    fun cancelPipeline(projectId: Int, pipelineId: Int): Pipeline {
        logger.debug("Pipelines service call: cancel pipeline projectId={} pipelineId={}", projectId, pipelineId)
        verifyWritable("can't cancel pipeline when in 'read only' mode")

        return runGitLabCall {
            gitLabApi.pipelineApi.cancelPipelineJobs(projectId, pipelineId.toLong()).toApiModel()
        }
    }

    fun getPipelines(projectId: Int, source: Source?): List<Pipeline> {
        logger.debug("Pipelines service call: get pipelines projectId={} source={}", projectId, source)

        val updatedAfter = Date.from(Instant.now().minus(properties.pipelineHistoryDays, ChronoUnit.DAYS))
        val filter = PipelineFilter().withUpdatedAfter(updatedAfter)

        return runGitLabCall {
            val pipelines = gitLabApi.pipelineApi.getPipelines(projectId, filter).map { it.toApiModel() }
            if (source == null) {
                pipelines
            } else {
                pipelines.filter { it.source == source }
            }
        }
    }

    fun retryPipeline(projectId: Int, pipelineId: Int): Pipeline {
        logger.debug("Pipelines service call: retry pipeline projectId={} pipelineId={}", projectId, pipelineId)
        verifyWritable("can't retry pipeline when in 'read only' mode")

        return runGitLabCall {
            gitLabApi.pipelineApi.retryPipelineJob(projectId, pipelineId.toLong()).toApiModel()
        }
    }

    fun startPipeline(request: StartPipelineRequest): Pipeline {
        logger.debug("Pipelines service call: start pipeline projectId={} branch={}", request.projectId, request.branch)
        verifyWritable("can't start a new pipeline when in 'read only' mode")

        return runGitLabCall {
            gitLabApi.pipelineApi
                .createPipeline(request.projectId, request.branch, request.envVars ?: emptyMap())
                .toApiModel()
        }
    }

    fun getLatestPipeline(projectId: Int, branch: String): Pipeline? {
        val filter = PipelineFilter()
            .withRef(branch)
            .withOrderBy(Constants.PipelineOrderBy.UPDATED_AT)
            .withSort(Constants.SortOrder.DESC)

        return runGitLabCall {
            gitLabApi.pipelineApi
                .getPipelines(projectId, filter, 1)
                .first()
                .firstOrNull()
                ?.toApiModel()
        }
    }

    private fun verifyWritable(message: String) {
        if (properties.readOnly) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)
        }
    }
}
