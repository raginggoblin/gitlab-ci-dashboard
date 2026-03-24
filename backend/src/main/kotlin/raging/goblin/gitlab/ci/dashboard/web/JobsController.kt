package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.api.api.JobsApi
import raging.goblin.api.model.Job

@RestController
class JobsController : JobsApi {
    override fun getJobs(projectId: Int, pipelineId: Int, scope: String): ResponseEntity<List<Job>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
