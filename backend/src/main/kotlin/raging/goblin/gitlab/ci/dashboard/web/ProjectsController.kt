package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.gitlab.ci.dashboard.api.api.ProjectsApi
import raging.goblin.gitlab.ci.dashboard.api.model.ProjectPipeline
import raging.goblin.gitlab.ci.dashboard.api.model.ProjectPipelines

@RestController
class ProjectsController : ProjectsApi {
    override fun getProjectsWithLatestPipeline(groupId: Int, projectIds: String?): ResponseEntity<List<ProjectPipeline>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    override fun getProjectsWithPipelines(groupId: Int, projectIds: String?): ResponseEntity<List<ProjectPipelines>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
