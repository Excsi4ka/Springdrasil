package dev.excsi.springdrasil.configuration

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.excsi.springdrasil.dto.JoinSessionData
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CacheConfiguration {

    @Bean
    fun sessionCache(): Cache<String, JoinSessionData> {
        return Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .maximumSize(1000)
            .build()
    }
}