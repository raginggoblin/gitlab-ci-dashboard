package larscom.gitlab.ci.dashboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitlabCiDashboardApplication

fun main(args: Array<String>) {
    runApplication<larscom.gitlab.ci.dashboard.GitlabCiDashboardApplication>(*args)
}
