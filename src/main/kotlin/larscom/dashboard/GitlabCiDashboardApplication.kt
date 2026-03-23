package larscom.dashboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitlabCiDashboardApplication

fun main(args: Array<String>) {
	runApplication<GitlabCiDashboardApplication>(*args)
}
