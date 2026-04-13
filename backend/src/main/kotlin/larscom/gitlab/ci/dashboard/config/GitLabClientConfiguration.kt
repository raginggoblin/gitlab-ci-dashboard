package larscom.gitlab.ci.dashboard.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

@Configuration
class GitLabClientConfiguration(private val properties: DashboardProperties) {
    @Bean
    fun gitLabRestClient(): RestClient {
        return RestClient.builder()
            .baseUrl("${properties.gitlabBaseUrl.trimEnd('/')}/api/v4")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("PRIVATE-TOKEN", properties.gitlabApiToken)
            .build()
    }
}
