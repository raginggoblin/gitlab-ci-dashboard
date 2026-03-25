package raging.goblin.gitlab.ci.dashboard.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DashboardProperties(
    @Value($$"${gitlab-base-url}") val gitlabBaseUrl: String,
    @Value($$"${gitlab-api-token}") val gitlabApiToken: String,
    @Value($$"${version}") val apiVersion: String,
    @Value($$"${api-read-only}") val readOnly: Boolean,
    @Value($$"${ui-hide-write-actions}") val hideWriteActions: Boolean,
    @Value($$"${ui-page-size-options}") private val pageSizeOptionsRaw: String,
    @Value($$"${ui-default-page-size}") val defaultPageSize: Int,
    @Value($$"${gitlab-group-only-ids}") private val groupOnlyIdsRaw: String,
    @Value($$"${gitlab-group-skip-ids}") private val groupSkipIdsRaw: String,
    @Value($$"${gitlab-group-only-top-level}") val groupOnlyTopLevel: Boolean,
    @Value($$"${gitlab-group-include-subgroups}") val groupIncludeSubgroups: Boolean,
    @Value($$"${gitlab-project-skip-ids}") private val projectSkipIdsRaw: String,
    @Value($$"${gitlab-pipeline-history-days}") val pipelineHistoryDays: Long,
) {
    val pageSizeOptions: List<Int> = parseCsvInts(pageSizeOptionsRaw)
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
