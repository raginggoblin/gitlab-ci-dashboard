package larscom.gitlab.ci.dashboard.web.config

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import larscom.gitlab.ci.dashboard.api.api.ConfigApi
import larscom.gitlab.ci.dashboard.api.model.ApiConfig

@RestController
class ConfigController(private val configService: ConfigService) : ConfigApi {
    private val logger = LoggerFactory.getLogger(ConfigController::class.java)

    override fun getConfig(): ResponseEntity<ApiConfig> {
        logger.debug("Config controller call")
        return ResponseEntity.ok(configService.getConfig())
    }
}
