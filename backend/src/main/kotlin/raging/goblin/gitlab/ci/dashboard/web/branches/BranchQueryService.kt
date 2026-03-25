package raging.goblin.gitlab.ci.dashboard.web.branches

import org.gitlab4j.api.GitLabApi
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.Branch
import raging.goblin.gitlab.ci.dashboard.config.CacheNames
import raging.goblin.gitlab.ci.dashboard.mapping.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.mapping.toApiModel

@Service
class BranchQueryService(private val gitLabApi: GitLabApi) {
    @Cacheable(cacheNames = [CacheNames.BRANCHES], key = "#projectId")
    fun getBranches(projectId: Int): List<Branch> {
        return runGitLabCall {
            gitLabApi.repositoryApi.getBranches(projectId).map { it.toApiModel() }
        }
    }
}
