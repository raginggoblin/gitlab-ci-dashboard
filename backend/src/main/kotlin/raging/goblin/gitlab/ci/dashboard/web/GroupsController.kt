package raging.goblin.gitlab.ci.dashboard.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.gitlab.ci.dashboard.api.api.GroupsApi
import raging.goblin.gitlab.ci.dashboard.api.model.Group

@RestController
class GroupsController : GroupsApi {
    override fun getGroups(): ResponseEntity<List<Group>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
