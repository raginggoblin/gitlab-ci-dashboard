package larscom.gitlab.ci.dashboard.web.config

import org.slf4j.LoggerFactory
import org.springframework.boot.info.BuildProperties
import org.springframework.stereotype.Service
import larscom.gitlab.ci.dashboard.api.model.ApiConfig
import larscom.gitlab.ci.dashboard.config.DashboardProperties

@Service
class ConfigService(
    private val properties: DashboardProperties,
    private val buildProperties: BuildProperties?,
) {
    private val logger = LoggerFactory.getLogger(ConfigService::class.java)

    fun getConfig(): ApiConfig {
        logger.debug("Config service call")
        val apiVersion = buildProperties?.version ?: javaClass.`package`?.implementationVersion ?: "dev"
        return ApiConfig(
            apiVersion = apiVersion,
            readOnly = properties.readOnly,
            hideWriteActions = properties.hideWriteActions,
            pageSizeOptions = properties.pageSizeOptions,
            defaultPageSize = properties.defaultPageSize,
            fetchRefreshInterval = properties.fetchRefreshInterval,
        )
    }
}
