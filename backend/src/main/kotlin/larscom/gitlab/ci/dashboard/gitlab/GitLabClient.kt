package larscom.gitlab.ci.dashboard.gitlab

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import java.time.Instant
import java.time.format.DateTimeFormatter

@Component
class GitLabClient(private val restClient: RestClient) {
    fun getGroups(topLevelOnly: Boolean, skipGroupIds: Set<Int>): List<GitLabGroup> {
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("top_level_only", topLevelOnly.toString())
        if (skipGroupIds.isNotEmpty()) {
            queryParams.add("skip_groups", skipGroupIds.joinToString(","))
        }

        return getAllPages("/groups", queryParams)
    }

    fun getGroupProjects(groupId: Int, includeSubgroups: Boolean): List<GitLabProject> {
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("archived", "false")
        queryParams.add("include_subgroups", includeSubgroups.toString())
        return getAllPages("/groups/{groupId}/projects", queryParams, groupId)
    }

    fun getPipelines(projectId: Int, updatedAfter: Instant?): List<GitLabPipeline> {
        val queryParams = LinkedMultiValueMap<String, String>()
        if (updatedAfter != null) {
            queryParams.add("updated_after", DateTimeFormatter.ISO_INSTANT.format(updatedAfter))
        }
        return getAllPages("/projects/{projectId}/pipelines", queryParams, projectId)
    }

    fun getLatestPipeline(projectId: Int, branch: String): GitLabPipeline? {
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("ref", branch)
        queryParams.add("order_by", "updated_at")
        queryParams.add("sort", "desc")
        queryParams.add("per_page", "1")

        return getPage<GitLabPipeline>(
            path = "/projects/{projectId}/pipelines",
            queryParams = queryParams,
            page = 1,
            uriVariables = arrayOf(projectId),
        ).body.firstOrNull()
    }

    fun cancelPipeline(projectId: Int, pipelineId: Int): GitLabPipeline {
        return restClient.post()
            .uri("/projects/{projectId}/pipelines/{pipelineId}/cancel", projectId, pipelineId)
            .retrieve()
            .body(GitLabPipeline::class.java)
            ?: GitLabPipeline()
    }

    fun retryPipeline(projectId: Int, pipelineId: Int): GitLabPipeline {
        return restClient.post()
            .uri("/projects/{projectId}/pipelines/{pipelineId}/retry", projectId, pipelineId)
            .retrieve()
            .body(GitLabPipeline::class.java)
            ?: GitLabPipeline()
    }

    fun createPipeline(projectId: Int, branch: String, envVars: Map<String, String>): GitLabPipeline {
        val request = if (envVars.isEmpty()) {
            null
        } else {
            CreatePipelineRequest(
                variables = envVars.entries.map { (key, value) -> CreatePipelineVariable(key = key, value = value) },
            )
        }

        val requestSpec = restClient.post().uri { uriBuilder ->
            uriBuilder
                .path("/projects/{projectId}/pipeline")
                .queryParam("ref", branch)
                .build(projectId)
        }

        val response = if (request == null) {
            requestSpec.retrieve()
        } else {
            requestSpec.body(request).retrieve()
        }

        return response.body(GitLabPipeline::class.java) ?: GitLabPipeline()
    }

    fun getJobsForPipeline(projectId: Int, pipelineId: Int, scopes: List<String>): List<GitLabJob> {
        val queryParams = LinkedMultiValueMap<String, String>()
        scopes.forEach { scope -> queryParams.add("scope[]", scope) }

        return getAllPages(
            path = "/projects/{projectId}/pipelines/{pipelineId}/jobs",
            queryParams = queryParams,
            projectId,
            pipelineId,
        )
    }

    fun getBranches(projectId: Int): List<GitLabBranch> {
        return getAllPages("/projects/{projectId}/repository/branches", LinkedMultiValueMap(), projectId)
    }

    fun getPipelineSchedules(projectId: Int): List<GitLabSchedule> {
        return getAllPages("/projects/{projectId}/pipeline_schedules", LinkedMultiValueMap(), projectId)
    }

    fun downloadArtifact(projectId: Int, jobId: Int): ByteArray {
        return restClient.get()
            .uri("/projects/{projectId}/jobs/{jobId}/artifacts", projectId, jobId)
            .retrieve()
            .body(ByteArray::class.java)
            ?: ByteArray(0)
    }

    private inline fun <reified T> getAllPages(
        path: String,
        queryParams: LinkedMultiValueMap<String, String>,
        vararg uriVariables: Any,
    ): List<T> {
        val allItems = mutableListOf<T>()
        var page = 1
        var totalPages = 1

        do {
            val pageResult = getPage<T>(path, queryParams, page, uriVariables)
            allItems.addAll(pageResult.body)
            totalPages = pageResult.totalPages
            page++
        } while (page <= totalPages)

        return allItems
    }

    private inline fun <reified T> getPage(
        path: String,
        queryParams: LinkedMultiValueMap<String, String>,
        page: Int,
        uriVariables: Array<out Any>,
    ): PageResult<T> {
        val pageQueryParams = LinkedMultiValueMap(queryParams)
        if (!pageQueryParams.containsKey("page")) {
            pageQueryParams.add("page", page.toString())
        }
        if (!pageQueryParams.containsKey("per_page")) {
            pageQueryParams.add("per_page", "100")
        }

        val entity = restClient.get()
            .uri { uriBuilder ->
                uriBuilder.path(path)
                pageQueryParams.forEach { (key, values) ->
                    values.forEach { value -> uriBuilder.queryParam(key, value) }
                }
                uriBuilder.build(*uriVariables)
            }
            .retrieve()
            .toEntity(object : ParameterizedTypeReference<List<T>>() {})

        val body = entity.body ?: emptyList()
        val totalPages = entity.headers.parseTotalPages()
        return PageResult(body = body, totalPages = totalPages)
    }

    private data class PageResult<T>(
        val body: List<T>,
        val totalPages: Int,
    )

    private fun HttpHeaders.parseTotalPages(): Int {
        val value = getFirst("x-total-pages") ?: getFirst("X-Total-Pages")
        return value?.toIntOrNull()?.takeIf { it > 0 } ?: 1
    }

    private data class CreatePipelineRequest(
        val variables: List<CreatePipelineVariable>,
    )

    private data class CreatePipelineVariable(
        val key: String,
        val value: String,
    )
}
