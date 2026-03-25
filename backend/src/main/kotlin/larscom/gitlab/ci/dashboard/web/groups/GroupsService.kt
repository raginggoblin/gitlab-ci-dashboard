package larscom.gitlab.ci.dashboard.web.groups

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.GroupFilter
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.Group
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.config.DashboardProperties
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel

@Service
class GroupsService(
    private val gitLabApi: GitLabApi,
    private val properties: DashboardProperties,
) {
    private val logger = LoggerFactory.getLogger(GroupsService::class.java)

    @Cacheable(cacheNames = [CacheNames.GROUPS])
    fun getGroups(): List<Group> {
        logger.debug("Groups service call")

        val groupFilter = GroupFilter()
            .withTopLevelOnly(properties.groupOnlyTopLevel)
            .withSkipGroups(properties.groupSkipIds.map { it.toLong() })

        return runGitLabCall {
            gitLabApi.groupApi
                .getGroups(groupFilter)
                .filter { properties.groupOnlyIds.isEmpty() || properties.groupOnlyIds.contains((it.id ?: 0L).toInt()) }
                .map { it.toApiModel() }
                .sortedBy { it.name.lowercase() }
        }
    }
}
