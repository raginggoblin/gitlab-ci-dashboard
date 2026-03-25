package raging.goblin.gitlab.ci.dashboard.projects

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.GroupProjectsFilter
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.Pipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Project
import raging.goblin.gitlab.ci.dashboard.api.model.ProjectPipeline
import raging.goblin.gitlab.ci.dashboard.api.model.ProjectPipelines
import raging.goblin.gitlab.ci.dashboard.api.model.Status
import raging.goblin.gitlab.ci.dashboard.jobs.JobsService
import raging.goblin.gitlab.ci.dashboard.pipelines.PipelinesService
import raging.goblin.gitlab.ci.dashboard.support.DashboardProperties
import raging.goblin.gitlab.ci.dashboard.support.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.support.toApiModel

@Service
class ProjectsService(
    private val gitLabApi: GitLabApi,
    private val pipelinesService: PipelinesService,
    private val jobsService: JobsService,
    private val properties: DashboardProperties,
) {
    private val logger = LoggerFactory.getLogger(ProjectsService::class.java)

    fun getProjectsWithLatestPipeline(groupId: Int, projectIdsCsv: String?): List<ProjectPipeline> {
        logger.debug("Projects service call: latest pipeline groupId={} projectIds={}", groupId, projectIdsCsv)

        return getProjects(groupId, projectIdsCsv)
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

        return getProjects(groupId, projectIdsCsv).map { project ->
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

    fun getProjects(groupId: Int, projectIdsCsv: String?): List<Project> {
        val requestedProjectIds = parseCsvIds(projectIdsCsv)

        val filter = GroupProjectsFilter()
            .withIncludeSubGroups(properties.groupIncludeSubgroups)

        return runGitLabCall {
            gitLabApi.groupApi
                .getProjects(groupId, filter)
                .filter { project -> !properties.projectSkipIds.contains((project.id ?: 0L).toInt()) }
                .filter { project -> requestedProjectIds == null || requestedProjectIds.contains((project.id ?: 0L).toInt()) }
                .map { it.toApiModel() }
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

    private fun parseCsvIds(csv: String?): Set<Int>? {
        if (csv.isNullOrBlank()) {
            return null
        }
        return csv
            .split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }
}
