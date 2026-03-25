package larscom.gitlab.ci.dashboard.web.artifacts

import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall

@Service
class ArtifactsService(private val gitLabApi: GitLabApi) {
    private val logger = LoggerFactory.getLogger(ArtifactsService::class.java)

    @Cacheable(cacheNames = [CacheNames.ARTIFACTS], key = "#projectId + ':' + #jobId")
    fun downloadArtifact(projectId: Int, jobId: Int): Resource {
        logger.debug("Artifacts service call: projectId={} jobId={}", projectId, jobId)
        return runGitLabCall {
            val artifactBytes = gitLabApi.jobApi.downloadArtifactsFile(projectId, jobId.toLong()).use { it.readBytes() }
            ByteArrayResource(artifactBytes)
        }
    }
}
