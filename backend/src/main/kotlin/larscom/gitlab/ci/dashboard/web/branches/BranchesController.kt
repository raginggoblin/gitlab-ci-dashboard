package larscom.gitlab.ci.dashboard.web.branches

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import larscom.gitlab.ci.dashboard.api.api.BranchesApi
import larscom.gitlab.ci.dashboard.api.model.Branch
import larscom.gitlab.ci.dashboard.api.model.BranchPipeline

@RestController
class BranchesController(private val branchesService: BranchesService) : BranchesApi {
    private val logger = LoggerFactory.getLogger(BranchesController::class.java)

    override fun getBranches(projectId: Int): ResponseEntity<List<Branch>> {
        logger.debug("Branches controller call: get branches projectId={}", projectId)
        return ResponseEntity.ok(branchesService.getBranches(projectId))
    }

    override fun getBranchesWithLatestPipeline(projectId: Int): ResponseEntity<List<BranchPipeline>> {
        logger.debug("Branches controller call: get branches with latest pipeline projectId={}", projectId)
        return ResponseEntity.ok(branchesService.getBranchesWithLatestPipeline(projectId))
    }
}
