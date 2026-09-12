package dev.excsi.springdrasil

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import java.util.UUID

/**
 * Yggdrasil(Authlib-injector) implementation spec: https://yushijinhun.github.io/authlib-injector/en/yggdrasil-server-technical-specification.html
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class SpringdrasilApplication

fun main(args: Array<String>) {
	runApplication<SpringdrasilApplication>(*args)
}

fun UUID.unhyphenatedString(): String {
	return this.toString().replace("-", "")
}
