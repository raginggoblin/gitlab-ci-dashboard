package raging.goblin.gitlab.ci.dashboard.support

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DashboardProperties(
    @Value("\${GITLAB_BASE_URL:https://gitlab.com}") val gitlabBaseUrl: String,
    @Value("\${GITLAB_API_TOKEN:}") val gitlabApiToken: String,
    @Value("\${VERSION:dev}") val apiVersion: String,
    @Value("\${API_READ_ONLY:true}") val readOnly: Boolean,
    @Value("\${UI_HIDE_WRITE_ACTIONS:false}") val hideWriteActions: Boolean,
    @Value("\${UI_PAGE_SIZE_OPTIONS:10,20,30,40,50}") private val pageSizeOptionsRaw: String,
    @Value("\${UI_DEFAULT_PAGE_SIZE:10}") val defaultPageSize: Int,
    @Value("\${GITLAB_GROUP_ONLY_IDS:}") private val groupOnlyIdsRaw: String,
    @Value("\${GITLAB_GROUP_SKIP_IDS:}") private val groupSkipIdsRaw: String,
    @Value("\${GITLAB_GROUP_ONLY_TOP_LEVEL:true}") val groupOnlyTopLevel: Boolean,
    @Value("\${GITLAB_GROUP_INCLUDE_SUBGROUPS:true}") val groupIncludeSubgroups: Boolean,
    @Value("\${GITLAB_PROJECT_SKIP_IDS:}") private val projectSkipIdsRaw: String,
    @Value("\${GITLAB_PIPELINE_HISTORY_DAYS:5}") val pipelineHistoryDays: Long,
) {
    val pageSizeOptions: List<Int> = parseCsvInts(pageSizeOptionsRaw).ifEmpty { listOf(10, 20, 30, 40, 50) }
    val groupOnlyIds: Set<Int> = parseCsvInts(groupOnlyIdsRaw).toSet()
    val groupSkipIds: Set<Int> = parseCsvInts(groupSkipIdsRaw).toSet()
    val projectSkipIds: Set<Int> = parseCsvInts(projectSkipIdsRaw).toSet()

    companion object {
        fun parseCsvInts(value: String?): List<Int> {
            if (value.isNullOrBlank()) {
                return emptyList()
            }
            return value
                .split(',')
                .mapNotNull { token -> token.trim().toIntOrNull() }
        }
    }
}
