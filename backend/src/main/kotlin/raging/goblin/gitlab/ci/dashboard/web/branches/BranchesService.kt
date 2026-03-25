package raging.goblin.gitlab.ci.dashboard.web.branches

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.BranchPipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Pipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Status
import raging.goblin.gitlab.ci.dashboard.web.jobs.JobsService
import raging.goblin.gitlab.ci.dashboard.web.pipelines.PipelinesService

@Service
class BranchesService(
    private val branchQueryService: BranchQueryService,
    private val pipelinesService: PipelinesService,
    private val jobsService: JobsService,
) {
    private val logger = LoggerFactory.getLogger(BranchesService::class.java)

    fun getBranches(projectId: Int) = branchQueryService.getBranches(projectId)

    fun getBranchesWithLatestPipeline(projectId: Int): List<BranchPipeline> {
        logger.debug("Branches service call: get branches projectId={}", projectId)
        return branchQueryService.getBranches(projectId)
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
