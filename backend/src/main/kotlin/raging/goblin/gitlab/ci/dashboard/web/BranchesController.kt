package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.api.api.BranchesApi
import raging.goblin.api.model.Branch
import raging.goblin.api.model.BranchPipeline

@RestController
class BranchesController : BranchesApi {
    override fun getBranches(projectId: Int): ResponseEntity<List<Branch>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    override fun getBranchesWithLatestPipeline(projectId: Int): ResponseEntity<List<BranchPipeline>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
