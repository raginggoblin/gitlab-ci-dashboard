package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.gitlab.ci.dashboard.api.api.SchedulesApi
import raging.goblin.gitlab.ci.dashboard.api.model.ScheduleProjectPipeline

@RestController
class SchedulesController : SchedulesApi {
    override fun getSchedulesWithLatestPipelines(groupId: Int, projectIds: String?): ResponseEntity<List<ScheduleProjectPipeline>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
