package larscom.gitlab.ci.dashboard.web.projects

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.Project
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.config.DashboardProperties
import larscom.gitlab.ci.dashboard.gitlab.GitLabClient
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel

@Service
class ProjectQueryService(
    private val gitLabClient: GitLabClient,
    private val properties: DashboardProperties,
) {
    @Cacheable(
        cacheNames = [CacheNames.PROJECTS],
        key = "#groupId + ':' + (#projectIdsCsv == null ? '' : #projectIdsCsv.replaceAll('\\s+', ''))",
    )
    fun getProjects(groupId: Int, projectIdsCsv: String?): List<Project> {
        val requestedProjectIds = parseCsvIds(projectIdsCsv)

        return runGitLabCall {
            gitLabClient
                .getGroupProjects(
                    groupId = groupId,
                    includeSubgroups = properties.groupIncludeSubgroups,
                )
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
