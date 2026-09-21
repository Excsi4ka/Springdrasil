package dev.excsi.springdrasil.component

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@ConditionalOnProperty(
    prefix = "springdrasil.web",
    name = ["frontend-enabled"],
    havingValue = "true",
    matchIfMissing = true
)
annotation class ConditionalOnWebEnabled()
