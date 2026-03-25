package larscom.gitlab.ci.dashboard.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(properties: DashboardProperties): CacheManager {
        val cacheManager = SimpleCacheManager()
        cacheManager.setCaches(
            listOf(
                cache(CacheNames.GROUPS, properties.groupCacheTtlSeconds),
                cache(CacheNames.PROJECTS, properties.projectCacheTtlSeconds),
                cache(CacheNames.PIPELINES, properties.pipelineCacheTtlSeconds),
                cache(CacheNames.BRANCHES, properties.branchCacheTtlSeconds),
                cache(CacheNames.SCHEDULES, properties.scheduleCacheTtlSeconds),
                cache(CacheNames.JOBS, properties.jobCacheTtlSeconds),
                cache(CacheNames.ARTIFACTS, properties.artifactCacheTtlSeconds),
            ),
        )
        return cacheManager
    }

    private fun cache(name: String, ttlSeconds: Long): CaffeineCache {
        val ttl = ttlSeconds.coerceAtLeast(1)
        return CaffeineCache(
            name,
            Caffeine.newBuilder()
                .expireAfterWrite(ttl, TimeUnit.SECONDS)
                .build<Any, Any>(),
        )
    }
}
