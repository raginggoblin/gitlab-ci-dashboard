package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.gitlab.ci.dashboard.api.api.PipelinesApi
import raging.goblin.gitlab.ci.dashboard.api.model.Pipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Source
import raging.goblin.gitlab.ci.dashboard.api.model.StartPipelineRequest

@RestController
class PipelinesController : PipelinesApi {
    override fun cancelPipeline(projectId: Int, pipelineId: Int): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    override fun getPipelines(projectId: Int, source: Source?): ResponseEntity<List<Pipeline>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    override fun retryPipeline(projectId: Int, pipelineId: Int): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    override fun startPipeline(startPipelineRequest: StartPipelineRequest): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
