package larscom.gitlab.ci.dashboard.config

import org.gitlab4j.api.GitLabApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import larscom.gitlab.ci.dashboard.config.DashboardProperties

@Configuration
class GitLabClientConfiguration(private val properties: DashboardProperties) {
    @Bean
    fun gitLabApi(): GitLabApi {
        return GitLabApi(properties.gitlabBaseUrl, properties.gitlabApiToken)
    }
}
