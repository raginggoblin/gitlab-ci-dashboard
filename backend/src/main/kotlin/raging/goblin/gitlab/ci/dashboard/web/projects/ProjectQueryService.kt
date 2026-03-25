package raging.goblin.gitlab.ci.dashboard.web.projects

import org.gitlab4j.api.GitLabApi
import org.gitlab4j.api.models.GroupProjectsFilter
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.Project
import raging.goblin.gitlab.ci.dashboard.config.CacheNames
import raging.goblin.gitlab.ci.dashboard.config.DashboardProperties
import raging.goblin.gitlab.ci.dashboard.mapping.runGitLabCall
import raging.goblin.gitlab.ci.dashboard.mapping.toApiModel

@Service
class ProjectQueryService(
    private val gitLabApi: GitLabApi,
    private val properties: DashboardProperties,
) {
    @Cacheable(
        cacheNames = [CacheNames.PROJECTS],
        key = "#groupId + ':' + (#projectIdsCsv == null ? '' : #projectIdsCsv.replaceAll('\\s+', ''))",
    )
    fun getProjects(groupId: Int, projectIdsCsv: String?): List<Project> {
        val requestedProjectIds = parseCsvIds(projectIdsCsv)

        val filter = GroupProjectsFilter()
            .withIncludeSubGroups(properties.groupIncludeSubgroups)

        return runGitLabCall {
            gitLabApi.groupApi
                .getProjects(groupId, filter)
                .filter { project -> !properties.projectSkipIds.contains((project.id ?: 0L).toInt()) }
                .filter { project -> requestedProjectIds == null || requestedProjectIds.contains((project.id ?: 0L).toInt()) }
                .map { it.toApiModel() }
        }
    }

    private fun parseCsvIds(csv: String?): Set<Int>? {
        if (csv.isNullOrBlank()) {
            return null
        }
        return csv
            .split(',')
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
    }
}
