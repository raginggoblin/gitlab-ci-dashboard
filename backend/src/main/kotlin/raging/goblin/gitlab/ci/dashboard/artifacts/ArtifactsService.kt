package raging.goblin.gitlab.ci.dashboard.artifacts

import org.gitlab4j.api.GitLabApi
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.support.runGitLabCall

@Service
class ArtifactsService(private val gitLabApi: GitLabApi) {
    private val logger = LoggerFactory.getLogger(ArtifactsService::class.java)

    fun downloadArtifact(projectId: Int, jobId: Int): Resource {
        logger.debug("Artifacts service call: projectId={} jobId={}", projectId, jobId)
        return runGitLabCall {
            InputStreamResource(gitLabApi.jobApi.downloadArtifactsFile(projectId, jobId.toLong()))
        }
    }
}
