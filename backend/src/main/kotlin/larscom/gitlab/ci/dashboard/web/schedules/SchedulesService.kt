package larscom.gitlab.ci.dashboard.web.schedules

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.ScheduleProjectPipeline
import larscom.gitlab.ci.dashboard.api.model.Status
import larscom.gitlab.ci.dashboard.web.jobs.JobsService
import larscom.gitlab.ci.dashboard.web.pipelines.PipelinesService
import larscom.gitlab.ci.dashboard.web.projects.ProjectQueryService

@Service
class SchedulesService(
    private val projectQueryService: ProjectQueryService,
    private val scheduleQueryService: ScheduleQueryService,
    private val pipelinesService: PipelinesService,
    private val jobsService: JobsService,
) {
    private val logger = LoggerFactory.getLogger(SchedulesService::class.java)

    fun getSchedulesWithLatestPipelines(groupId: Int, projectIdsCsv: String?): List<ScheduleProjectPipeline> {
        logger.debug("Schedules service call: groupId={} projectIds={}", groupId, projectIdsCsv)

        return projectQueryService
            .getProjects(groupId, projectIdsCsv)
            .flatMap { project ->
                val schedules = if (project.defaultBranch.isNotBlank()) scheduleQueryService.getSchedules(project.id) else emptyList()

                schedules.map { schedule ->
                    val pipeline = pipelinesService.getLatestPipeline(project.id, schedule.ref)
                    val failedJobs = if (pipeline?.status == Status.FAILED) {
                        jobsService.getFailedJobs(project.id, pipeline.id)
                    } else {
                        null
                    }

                    ScheduleProjectPipeline(
                        groupId = groupId,
                        schedule = schedule,
                        project = project,
                        pipeline = pipeline,
                        failedJobs = failedJobs,
                    )
                }
            }
            .sortedBy { it.schedule.id }
    }
}
