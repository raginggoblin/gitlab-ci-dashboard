package larscom.gitlab.ci.dashboard.web.projects

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.Pipeline
import larscom.gitlab.ci.dashboard.api.model.ProjectPipeline
import larscom.gitlab.ci.dashboard.api.model.ProjectPipelines
import larscom.gitlab.ci.dashboard.api.model.Status
import larscom.gitlab.ci.dashboard.web.jobs.JobsService
import larscom.gitlab.ci.dashboard.web.pipelines.PipelinesService

@Service
class ProjectsService(
    private val projectQueryService: ProjectQueryService,
    private val pipelinesService: PipelinesService,
    private val jobsService: JobsService,
) {
    private val logger = LoggerFactory.getLogger(ProjectsService::class.java)

    fun getProjectsWithLatestPipeline(groupId: Int, projectIdsCsv: String?): List<ProjectPipeline> {
        logger.debug("Projects service call: latest pipeline groupId={} projectIds={}", groupId, projectIdsCsv)

        return projectQueryService.getProjects(groupId, projectIdsCsv)
            .map { project ->
                val branch = project.defaultBranch.takeIf { it.isNotBlank() }
                val pipeline = branch?.let { pipelinesService.getLatestPipeline(project.id, it) }
                val failedJobs = if (pipeline?.status == Status.FAILED) {
                    jobsService.getFailedJobs(project.id, pipeline.id)
                } else {
                    null
                }

                ProjectPipeline(
                    groupId = groupId,
                    project = project,
                    pipeline = pipeline,
                    failedJobs = failedJobs,
                )
            }
            .sortedWith { left, right -> comparePipelines(left.pipeline, right.pipeline) }
    }

    fun getProjectsWithPipelines(groupId: Int, projectIdsCsv: String?): List<ProjectPipelines> {
        logger.debug("Projects service call: pipelines groupId={} projectIds={}", groupId, projectIdsCsv)

        return projectQueryService.getProjects(groupId, projectIdsCsv).map { project ->
            val pipelines = if (project.defaultBranch.isNotBlank()) {
                pipelinesService.getPipelines(project.id, null)
            } else {
                emptyList()
            }

            ProjectPipelines(
                groupId = groupId,
                project = project,
                pipelines = pipelines,
            )
        }
    }

    private fun comparePipelines(left: Pipeline?, right: Pipeline?): Int {
        return when {
            left == null && right != null -> -1
            left != null && right == null -> 1
            left == null -> 0
            else -> right!!.updatedAt.compareTo(left.updatedAt)
        }
    }
}
