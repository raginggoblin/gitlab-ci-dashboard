package larscom.gitlab.ci.dashboard.mapping

import larscom.gitlab.ci.dashboard.api.model.Branch
import larscom.gitlab.ci.dashboard.api.model.Commit
import larscom.gitlab.ci.dashboard.api.model.Group
import larscom.gitlab.ci.dashboard.api.model.Job
import larscom.gitlab.ci.dashboard.api.model.Namespace
import larscom.gitlab.ci.dashboard.api.model.Pipeline
import larscom.gitlab.ci.dashboard.api.model.Project
import larscom.gitlab.ci.dashboard.api.model.Schedule
import larscom.gitlab.ci.dashboard.api.model.Source
import larscom.gitlab.ci.dashboard.api.model.Status
import larscom.gitlab.ci.dashboard.api.model.User
import larscom.gitlab.ci.dashboard.gitlab.GitLabBranch
import larscom.gitlab.ci.dashboard.gitlab.GitLabCommit
import larscom.gitlab.ci.dashboard.gitlab.GitLabGroup
import larscom.gitlab.ci.dashboard.gitlab.GitLabJob
import larscom.gitlab.ci.dashboard.gitlab.GitLabNamespace
import larscom.gitlab.ci.dashboard.gitlab.GitLabPipeline
import larscom.gitlab.ci.dashboard.gitlab.GitLabProject
import larscom.gitlab.ci.dashboard.gitlab.GitLabSchedule
import larscom.gitlab.ci.dashboard.gitlab.GitLabUser
import java.math.BigDecimal
import java.net.URI
import java.time.Instant

fun GitLabGroup.toApiModel(): Group = Group(
    id = (id ?: 0L).toInt(),
    name = name.orEmpty(),
)

fun GitLabProject.toApiModel(): Project = Project(
    id = (id ?: 0L).toInt(),
    name = name.orEmpty(),
    defaultBranch = defaultBranch.orEmpty(),
    webUrl = safeUri(webUrl),
    topics = topics ?: emptyList(),
    namespace = namespace.toApiModel(),
    description = description,
)

fun GitLabNamespace?.toApiModel(): Namespace = Namespace(
    id = ((this?.id) ?: 0L).toInt(),
    name = this?.name.orEmpty(),
    path = this?.path.orEmpty(),
)

fun GitLabPipeline.toApiModel(): Pipeline = Pipeline(
    id = (id ?: 0L).toInt(),
    projectId = (projectId ?: 0L).toInt(),
    status = status.toApiStatus(),
    source = source.toApiModel(),
    ref = ref.orEmpty(),
    sha = sha.orEmpty(),
    webUrl = safeUri(webUrl),
    updatedAt = updatedAt ?: Instant.EPOCH,
    createdAt = createdAt ?: Instant.EPOCH,
    coverage = coverage?.toBigDecimalOrNull(),
)

fun GitLabJob.toApiModel(): Job = Job(
    id = (id ?: 0L).toInt(),
    commit = (commit ?: GitLabCommit()).toApiModel(),
    allowFailure = allowFailure ?: false,
    createdAt = createdAt ?: Instant.EPOCH,
    name = name.orEmpty(),
    pipeline = (pipeline ?: GitLabPipeline()).toApiModel(),
    ref = ref.orEmpty(),
    stage = stage.orEmpty(),
    status = status.toApiStatus(),
    webUrl = safeUri(webUrl),
    user = user.toApiModel(),
)

fun GitLabBranch.toApiModel(): Branch = Branch(
    name = name.orEmpty(),
    merged = merged ?: false,
    `protected` = isProtected ?: false,
    default = isDefault ?: false,
    canPush = canPush ?: false,
    webUrl = safeUri(webUrl),
    commit = (commit ?: GitLabCommit()).toApiModel(),
    pipeline = null,
)

fun GitLabSchedule.toApiModel(): Schedule = Schedule(
    id = (id ?: 0L).toInt(),
    description = description.orEmpty(),
    ref = ref.orEmpty(),
    cron = cron.orEmpty(),
    cronTimezone = cronTimezone.orEmpty(),
    nextRunAt = nextRunAt ?: Instant.EPOCH,
    active = active ?: false,
    createdAt = createdAt ?: Instant.EPOCH,
    updatedAt = updatedAt ?: Instant.EPOCH,
    owner = owner.toApiModel(),
)

fun GitLabCommit.toApiModel(): Commit = Commit(
    id = id.orEmpty(),
    authorName = authorName.orEmpty(),
    committerName = committerName.orEmpty(),
    committedDate = committedDate ?: Instant.EPOCH,
    title = title.orEmpty(),
    message = message.orEmpty(),
)

fun GitLabUser?.toApiModel(): User = User(
    id = ((this?.id) ?: 0L).toInt(),
    username = this?.username.orEmpty(),
    name = this?.name.orEmpty(),
    state = this?.state.orEmpty(),
    isAdmin = this?.isAdmin ?: false,
)

fun String?.toApiStatus(): Status = mapStatus(this)

fun String?.toApiModel(): Source {
    val value = this?.trim().orEmpty()
    return runCatching { Source.forValue(value) }.getOrDefault(Source.WEB)
}

fun mapStatus(value: String?): Status {
    val normalized = when (value?.lowercase()) {
        "canceling" -> "canceled"
        else -> value?.lowercase()
    }
    return runCatching { Status.forValue(normalized.orEmpty()) }.getOrDefault(Status.CREATED)
}

private fun safeUri(value: String?): URI = runCatching {
    URI.create(value ?: "about:blank")
}.getOrDefault(URI.create("about:blank"))

private fun String.toBigDecimalOrNull(): BigDecimal? = runCatching { BigDecimal(this) }.getOrNull()
