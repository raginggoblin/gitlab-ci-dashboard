package larscom.gitlab.ci.dashboard.web.artifacts

import larscom.gitlab.ci.dashboard.api.api.ArtifactsApi
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtifactsController(private val artifactsService: ArtifactsService) : ArtifactsApi {
    private val logger = LoggerFactory.getLogger(ArtifactsController::class.java)

    override fun downloadArtifact(projectId: Int, jobId: Int): ResponseEntity<Resource> {
        logger.debug("Artifacts controller call: projectId={} jobId={}", projectId, jobId)
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(artifactsService.downloadArtifact(projectId, jobId))
    }
}
