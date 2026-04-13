package larscom.gitlab.ci.dashboard.gitlab

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabGroup(
    val id: Long? = null,
    val name: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabNamespace(
    val id: Long? = null,
    val name: String? = null,
    val path: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabProject(
    val id: Long? = null,
    val name: String? = null,
    @JsonProperty("default_branch") val defaultBranch: String? = null,
    @JsonProperty("web_url") val webUrl: String? = null,
    val topics: List<String>? = null,
    val namespace: GitLabNamespace? = null,
    val description: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabPipeline(
    val id: Long? = null,
    @JsonProperty("project_id") val projectId: Long? = null,
    val status: String? = null,
    val source: String? = null,
    val ref: String? = null,
    val sha: String? = null,
    @JsonProperty("web_url") val webUrl: String? = null,
    @JsonProperty("updated_at") val updatedAt: Instant? = null,
    @JsonProperty("created_at") val createdAt: Instant? = null,
    val coverage: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabCommit(
    val id: String? = null,
    @JsonProperty("author_name") val authorName: String? = null,
    @JsonProperty("committer_name") val committerName: String? = null,
    @JsonProperty("committed_date") val committedDate: Instant? = null,
    val title: String? = null,
    val message: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabUser(
    val id: Long? = null,
    val username: String? = null,
    val name: String? = null,
    val state: String? = null,
    @JsonProperty("is_admin") val isAdmin: Boolean? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabJob(
    val id: Long? = null,
    val commit: GitLabCommit? = null,
    @JsonProperty("allow_failure") val allowFailure: Boolean? = null,
    @JsonProperty("created_at") val createdAt: Instant? = null,
    val name: String? = null,
    val pipeline: GitLabPipeline? = null,
    val ref: String? = null,
    val stage: String? = null,
    val status: String? = null,
    @JsonProperty("web_url") val webUrl: String? = null,
    val user: GitLabUser? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabBranch(
    val name: String? = null,
    val merged: Boolean? = null,
    @JsonProperty("protected") val isProtected: Boolean? = null,
    @JsonProperty("default") val isDefault: Boolean? = null,
    @JsonProperty("can_push") val canPush: Boolean? = null,
    @JsonProperty("web_url") val webUrl: String? = null,
    val commit: GitLabCommit? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitLabSchedule(
    val id: Long? = null,
    val description: String? = null,
    val ref: String? = null,
    val cron: String? = null,
    @JsonProperty("cron_timezone") val cronTimezone: String? = null,
    @JsonProperty("next_run_at") val nextRunAt: Instant? = null,
    val active: Boolean? = null,
    @JsonProperty("created_at") val createdAt: Instant? = null,
    @JsonProperty("updated_at") val updatedAt: Instant? = null,
    val owner: GitLabUser? = null,
)
