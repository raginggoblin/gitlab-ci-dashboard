package raging.goblin.gitlab.ci.dashboard.web.groups

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import raging.goblin.gitlab.ci.dashboard.api.api.GroupsApi
import raging.goblin.gitlab.ci.dashboard.api.model.Group

@RestController
class GroupsController(private val groupsService: GroupsService) : GroupsApi {
    private val logger = LoggerFactory.getLogger(GroupsController::class.java)

    override fun getGroups(): ResponseEntity<List<Group>> {
        logger.debug("Groups controller call")
        return ResponseEntity.ok(groupsService.getGroups())
    }
}
