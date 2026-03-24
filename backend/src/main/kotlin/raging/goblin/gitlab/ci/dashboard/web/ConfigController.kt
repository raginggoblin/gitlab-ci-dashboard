package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.api.api.ConfigApi
import raging.goblin.api.model.ApiConfig

@RestController
class ConfigController : ConfigApi {
    override fun getConfig(): ResponseEntity<ApiConfig> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
