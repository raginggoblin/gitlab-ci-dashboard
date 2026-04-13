package larscom.gitlab.ci.dashboard.web.groups

import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.Group
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.config.DashboardProperties
import larscom.gitlab.ci.dashboard.gitlab.GitLabClient
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel

@Service
class GroupsService(
    private val gitLabClient: GitLabClient,
    private val properties: DashboardProperties,
) {
    private val logger = LoggerFactory.getLogger(GroupsService::class.java)

    @Cacheable(cacheNames = [CacheNames.GROUPS])
    fun getGroups(): List<Group> {
        logger.debug("Groups service call")

        return runGitLabCall {
            gitLabClient
                .getGroups(
                    topLevelOnly = properties.groupOnlyTopLevel,
                    skipGroupIds = properties.groupSkipIds,
                )
                .filter { properties.groupOnlyIds.isEmpty() || properties.groupOnlyIds.contains((it.id ?: 0L).toInt()) }
                .map { it.toApiModel() }
                .sortedBy { it.name.lowercase() }
        }
    }
}
