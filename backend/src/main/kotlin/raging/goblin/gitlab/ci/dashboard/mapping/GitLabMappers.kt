package raging.goblin.gitlab.ci.dashboard.mapping

import org.gitlab4j.api.models.AbstractUser
import org.gitlab4j.api.models.JobStatus
import org.gitlab4j.api.models.PipelineSchedule
import org.gitlab4j.api.models.PipelineStatus
import raging.goblin.gitlab.ci.dashboard.api.model.Branch
import raging.goblin.gitlab.ci.dashboard.api.model.Commit
import raging.goblin.gitlab.ci.dashboard.api.model.Group
import raging.goblin.gitlab.ci.dashboard.api.model.Job
import raging.goblin.gitlab.ci.dashboard.api.model.Namespace
import raging.goblin.gitlab.ci.dashboard.api.model.Pipeline
import raging.goblin.gitlab.ci.dashboard.api.model.Project
import raging.goblin.gitlab.ci.dashboard.api.model.Schedule
import raging.goblin.gitlab.ci.dashboard.api.model.Source
import raging.goblin.gitlab.ci.dashboard.api.model.Status
import raging.goblin.gitlab.ci.dashboard.api.model.User
import java.math.BigDecimal
import java.net.URI
import java.time.Instant
import java.util.Date

fun org.gitlab4j.api.models.Group.toApiModel(): Group = Group(
    id = (id ?: 0L).toInt(),
    name = name.orEmpty(),
)

fun org.gitlab4j.api.models.Project.toApiModel(): Project = Project(
    id = (id ?: 0L).toInt(),
    name = name.orEmpty(),
    defaultBranch = defaultBranch.orEmpty(),
    webUrl = safeUri(webUrl),
    topics = topics ?: emptyList(),
    namespace = namespace.toApiModel(),
    description = description,
)

fun org.gitlab4j.api.models.Namespace?.toApiModel(): Namespace = Namespace(
    id = ((this?.id) ?: 0L).toInt(),
    name = this?.name.orEmpty(),
    path = this?.path.orEmpty(),
)

fun org.gitlab4j.api.models.Pipeline.toApiModel(): Pipeline = Pipeline(
    id = (id ?: 0L).toInt(),
    projectId = (projectId ?: 0L).toInt(),
    status = status.toApiModel(),
    source = source.toApiModel(),
    ref = ref.orEmpty(),
    sha = sha.orEmpty(),
    webUrl = safeUri(webUrl),
    updatedAt = updatedAt.toInstantOrEpoch(),
    createdAt = createdAt.toInstantOrEpoch(),
    coverage = coverage?.toBigDecimalOrNull(),
)

fun org.gitlab4j.api.models.Job.toApiModel(): Job = Job(
    id = (id ?: 0L).toInt(),
    commit = (commit ?: org.gitlab4j.api.models.Commit()).toApiModel(),
    allowFailure = allowFailure ?: false,
    createdAt = createdAt.toInstantOrEpoch(),
    name = name.orEmpty(),
    pipeline = (pipeline ?: org.gitlab4j.api.models.Pipeline()).toApiModel(),
    ref = ref.orEmpty(),
    stage = stage.orEmpty(),
    status = status.toApiModel(),
    webUrl = safeUri(webUrl),
    user = user.toApiModel(),
)

fun org.gitlab4j.api.models.Branch.toApiModel(): Branch = Branch(
    name = name.orEmpty(),
    merged = merged ?: false,
    `protected` = `protected` ?: false,
    default = default ?: false,
    canPush = canPush ?: false,
    webUrl = safeUri(webUrl),
    commit = (commit ?: org.gitlab4j.api.models.Commit()).toApiModel(),
    pipeline = null,
)

fun PipelineSchedule.toApiModel(): Schedule = Schedule(
    id = (id ?: 0L).toInt(),
    description = description.orEmpty(),
    ref = ref.orEmpty(),
    cron = cron.orEmpty(),
    cronTimezone = cronTimezone.orEmpty(),
    nextRunAt = nextRunAt.toInstantOrEpoch(),
    active = active ?: false,
    createdAt = createdAt.toInstantOrEpoch(),
    updatedAt = updatedAt.toInstantOrEpoch(),
    owner = owner.toApiModel(),
)

fun org.gitlab4j.api.models.Commit.toApiModel(): Commit = Commit(
    id = id.orEmpty(),
    authorName = authorName.orEmpty(),
    committerName = committerName.orEmpty(),
    committedDate = committedDate.toInstantOrEpoch(),
    title = title.orEmpty(),
    message = message.orEmpty(),
)

fun AbstractUser<*>?.toApiModel(): User = User(
    id = ((this?.id) ?: 0L).toInt(),
    username = this?.username.orEmpty(),
    name = this?.name.orEmpty(),
    state = this?.state.orEmpty(),
    isAdmin = (this as? org.gitlab4j.api.models.User)?.isAdmin ?: false,
)

fun PipelineStatus?.toApiModel(): Status = mapStatus(this?.toValue())

fun JobStatus?.toApiModel(): Status = mapStatus(this?.toValue())

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

private fun Date?.toInstantOrEpoch(): Instant = this?.toInstant() ?: Instant.EPOCH

private fun safeUri(value: String?): URI = runCatching {
    URI.create(value ?: "about:blank")
}.getOrDefault(URI.create("about:blank"))

private fun String.toBigDecimalOrNull(): BigDecimal? = runCatching { BigDecimal(this) }.getOrNull()
