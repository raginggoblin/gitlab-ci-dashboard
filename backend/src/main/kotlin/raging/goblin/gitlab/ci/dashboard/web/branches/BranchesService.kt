package raging.goblin.gitlab.ci.dashboard.web.branches

import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.Branch
import raging.goblin.gitlab.ci.dashboard.api.model.BranchPipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Pipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Status
import raging.goblin.gitlab.ci.dashboard.web.jobs.JobsService
import raging.goblin.gitlab.ci.dashboard.web.pipelines.PipelinesService
import raging.goblin.gitlab.ci.dashboard.mapping.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.mapping.toApiModel

@Service
class BranchesService(
    private val gitLabApi: GitLabApi,
    private val pipelinesService: PipelinesService,
    private val jobsService: JobsService,
) {
    private val logger = LoggerFactory.getLogger(BranchesService::class.java)

    fun getBranches(projectId: Int): List<Branch> {
        logger.debug("Branches service call: get branches projectId={}", projectId)
        return runGitLabCall {
            gitLabApi.repositoryApi.getBranches(projectId).map { it.toApiModel() }
        }
    }

    fun getBranchesWithLatestPipeline(projectId: Int): List<BranchPipeline> {
        logger.debug("Branches service call: get branches with latest pipeline projectId={}", projectId)

        return getBranches(projectId)
            .map { branch ->
                val pipeline = pipelinesService.getLatestPipeline(projectId, branch.name)
                val failedJobs = if (pipeline?.status == Status.FAILED) {
                    jobsService.getFailedJobs(projectId, pipeline.id)
                } else {
                    null
                }

                BranchPipeline(
                    branch = branch,
                    pipeline = pipeline,
                    failedJobs = failedJobs,
                )
            }
            .sortedWith { left, right -> comparePipelines(left.pipeline, right.pipeline) }
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
