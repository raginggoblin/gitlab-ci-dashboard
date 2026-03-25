package larscom.gitlab.ci.dashboard.web.projects

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import larscom.gitlab.ci.dashboard.api.api.ProjectsApi
import larscom.gitlab.ci.dashboard.api.model.ProjectPipeline
import larscom.gitlab.ci.dashboard.api.model.ProjectPipelines

@RestController
class ProjectsController(private val projectsService: ProjectsService) : ProjectsApi {
    private val logger = LoggerFactory.getLogger(ProjectsController::class.java)

    override fun getProjectsWithLatestPipeline(groupId: Int, projectIds: String?): ResponseEntity<List<ProjectPipeline>> {
        logger.debug("Projects controller call: latest pipeline groupId={} projectIds={}", groupId, projectIds)
        return ResponseEntity.ok(projectsService.getProjectsWithLatestPipeline(groupId, projectIds))
    }

    override fun getProjectsWithPipelines(groupId: Int, projectIds: String?): ResponseEntity<List<ProjectPipelines>> {
        logger.debug("Projects controller call: pipelines groupId={} projectIds={}", groupId, projectIds)
        return ResponseEntity.ok(projectsService.getProjectsWithPipelines(groupId, projectIds))
    }
}
