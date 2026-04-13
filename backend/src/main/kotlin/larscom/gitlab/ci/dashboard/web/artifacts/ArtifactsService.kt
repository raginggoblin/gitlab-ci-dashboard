package larscom.gitlab.ci.dashboard.web.artifacts

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.gitlab.GitLabClient
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall

@Service
class ArtifactsService(private val gitLabClient: GitLabClient) {
    private val logger = LoggerFactory.getLogger(ArtifactsService::class.java)

    @Cacheable(cacheNames = [CacheNames.ARTIFACTS], key = "#projectId + ':' + #jobId")
    fun downloadArtifact(projectId: Int, jobId: Int): Resource {
        logger.debug("Artifacts service call: projectId={} jobId={}", projectId, jobId)
        return runGitLabCall {
            ByteArrayResource(gitLabClient.downloadArtifact(projectId, jobId))
        }
    }
}
