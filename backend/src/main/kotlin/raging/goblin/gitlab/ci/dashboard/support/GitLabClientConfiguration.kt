package raging.goblin.gitlab.ci.dashboard.support

import org.gitlab4j.api.GitLabApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitLabClientConfiguration(private val properties: DashboardProperties) {
    @Bean
    fun gitLabApi(): GitLabApi {
        return GitLabApi(properties.gitlabBaseUrl, properties.gitlabApiToken)
    }
}
