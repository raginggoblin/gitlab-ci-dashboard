package larscom.gitlab.ci.dashboard.web.jobs

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import larscom.gitlab.ci.dashboard.api.api.JobsApi
import larscom.gitlab.ci.dashboard.api.model.Job

@RestController
class JobsController(private val jobsService: JobsService) : JobsApi {
    private val logger = LoggerFactory.getLogger(JobsController::class.java)

    override fun getJobs(projectId: Int, pipelineId: Int, scope: String): ResponseEntity<List<Job>> {
        logger.debug("Jobs controller call: projectId={} pipelineId={} scope={}", projectId, pipelineId, scope)
        return ResponseEntity.ok(jobsService.getJobs(projectId, pipelineId, scope))
    }
}
