package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.api.api.ArtifactsApi

@RestController
class ArtifactsController : ArtifactsApi {
    override fun downloadArtifact(projectId: Int, jobId: Int): ResponseEntity<org.springframework.core.io.Resource> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
