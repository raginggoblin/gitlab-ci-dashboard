package larscom.gitlab.ci.dashboard.web.schedules

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import larscom.gitlab.ci.dashboard.api.api.SchedulesApi
import larscom.gitlab.ci.dashboard.api.model.ScheduleProjectPipeline

@RestController
class SchedulesController(private val schedulesService: SchedulesService) : SchedulesApi {
    private val logger = LoggerFactory.getLogger(SchedulesController::class.java)

    override fun getSchedulesWithLatestPipelines(groupId: Int, projectIds: String?): ResponseEntity<List<ScheduleProjectPipeline>> {
        logger.debug("Schedules controller call: groupId={} projectIds={}", groupId, projectIds)
        return ResponseEntity.ok(schedulesService.getSchedulesWithLatestPipelines(groupId, projectIds))
    }
}
