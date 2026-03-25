package raging.goblin.gitlab.ci.dashboard.web.schedules

import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.ScheduleProjectPipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Status
import raging.goblin.gitlab.ci.dashboard.web.jobs.JobsService
import raging.goblin.gitlab.ci.dashboard.web.pipelines.PipelinesService
import raging.goblin.gitlab.ci.dashboard.web.projects.ProjectsService
import raging.goblin.gitlab.ci.dashboard.mapping.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.mapping.toApiModel

@Service
class SchedulesService(
    private val gitLabApi: GitLabApi,
    private val projectsService: ProjectsService,
    private val pipelinesService: PipelinesService,
    private val jobsService: JobsService,
) {
    private val logger = LoggerFactory.getLogger(SchedulesService::class.java)

    fun getSchedulesWithLatestPipelines(groupId: Int, projectIdsCsv: String?): List<ScheduleProjectPipeline> {
        logger.debug("Schedules service call: groupId={} projectIds={}", groupId, projectIdsCsv)

        return projectsService
            .getProjects(groupId, projectIdsCsv)
            .flatMap { project ->
                val schedules = if (project.defaultBranch.isNotBlank()) {
                    runGitLabCall {
                        gitLabApi.pipelineApi.getPipelineSchedules(project.id).map { it.toApiModel() }
                    }
                } else {
                    emptyList()
                }

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
