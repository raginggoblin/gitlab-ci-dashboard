package larscom.gitlab.ci.dashboard.web.schedules

import org.gitlab4j.api.GitLabApi
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.Schedule
import larscom.gitlab.ci.dashboard.config.CacheNames
import larscom.gitlab.ci.dashboard.mapping.runGitLabCall
import larscom.gitlab.ci.dashboard.mapping.toApiModel

@Service
class ScheduleQueryService(private val gitLabApi: GitLabApi) {
    @Cacheable(cacheNames = [CacheNames.SCHEDULES], key = "#projectId")
    fun getSchedules(projectId: Int): List<Schedule> {
        return runGitLabCall {
            gitLabApi.pipelineApi.getPipelineSchedules(projectId).map { it.toApiModel() }
        }
    }
}
