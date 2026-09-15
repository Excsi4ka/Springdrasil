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

private val unhyphenatedUuidRegex = Regex("[0-9a-fA-F]{32}")

fun UUID.unhyphenatedString(): String {
	return this.toString().replace("-", "")
}

fun String.isUnhyphenatedUuidValid(): Boolean = this.matches(unhyphenatedUuidRegex)

fun String.toUuid(): UUID {
	val hyphenated = substring(0, 8) + "-" +
			substring(8, 12) + "-" +
			substring(12, 16) + "-" +
			substring(16, 20) + "-" +
			substring(20, 32)

	return UUID.fromString(hyphenated)
}