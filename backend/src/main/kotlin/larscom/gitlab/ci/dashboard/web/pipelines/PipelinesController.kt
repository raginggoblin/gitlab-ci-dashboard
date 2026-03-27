package larscom.gitlab.ci.dashboard.web.pipelines

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import larscom.gitlab.ci.dashboard.api.api.PipelinesApi
import larscom.gitlab.ci.dashboard.api.model.Pipeline
import larscom.gitlab.ci.dashboard.api.model.Source
import larscom.gitlab.ci.dashboard.api.model.StartPipelineRequest

@RestController
class PipelinesController(private val pipelinesService: PipelinesService) : PipelinesApi {
    private val logger = LoggerFactory.getLogger(PipelinesController::class.java)

    override fun cancelPipeline(projectId: Int, pipelineId: Int): ResponseEntity<Unit> {
        logger.info("Pipelines controller call: cancel pipeline projectId={} pipelineId={}", projectId, pipelineId)
        pipelinesService.cancelPipeline(projectId, pipelineId)
        return ResponseEntity.ok().build()
    }

    override fun getPipelines(projectId: Int, source: Source?): ResponseEntity<List<Pipeline>> {
        logger.debug("Pipelines controller call: get pipelines projectId={} source={}", projectId, source)
        return ResponseEntity.ok(pipelinesService.getPipelines(projectId, source))
    }

    override fun retryPipeline(projectId: Int, pipelineId: Int): ResponseEntity<Unit> {
        logger.info("Pipelines controller call: retry pipeline projectId={} pipelineId={}", projectId, pipelineId)
        pipelinesService.retryPipeline(projectId, pipelineId)
        return ResponseEntity.ok().build()
    }

    override fun startPipeline(startPipelineRequest: StartPipelineRequest): ResponseEntity<Unit> {
        logger.info(
            "Pipelines controller call: start pipeline projectId={} branch={}",
            startPipelineRequest.projectId,
            startPipelineRequest.branch,
        )
        pipelinesService.startPipeline(startPipelineRequest)
        return ResponseEntity.ok().build()
    }
}
