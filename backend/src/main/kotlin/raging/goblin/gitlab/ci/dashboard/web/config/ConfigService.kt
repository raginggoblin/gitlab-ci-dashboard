package raging.goblin.gitlab.ci.dashboard.web.config

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import raging.goblin.gitlab.ci.dashboard.api.model.ApiConfig
import raging.goblin.gitlab.ci.dashboard.config.DashboardProperties

@Service
class ConfigService(private val properties: DashboardProperties) {
    private val logger = LoggerFactory.getLogger(ConfigService::class.java)

    fun getConfig(): ApiConfig {
        logger.debug("Config service call")
        return ApiConfig(
            apiVersion = properties.apiVersion,
            readOnly = properties.readOnly,
            hideWriteActions = properties.hideWriteActions,
            pageSizeOptions = properties.pageSizeOptions,
            defaultPageSize = properties.defaultPageSize,
        )
    }
}
