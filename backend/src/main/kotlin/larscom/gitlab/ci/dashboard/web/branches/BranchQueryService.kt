package larscom.gitlab.ci.dashboard.web.branches

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.Branch
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.gitlab.GitLabClient
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel

@Service
class BranchQueryService(private val gitLabClient: GitLabClient) {
    @Cacheable(cacheNames = [CacheNames.BRANCHES], key = "#projectId")
    fun getBranches(projectId: Int): List<Branch> {
        return runGitLabCall {
            gitLabClient.getBranches(projectId).map { it.toApiModel() }
        }
    }
}
