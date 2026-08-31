package dev.excsi.springdrasil

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.UUID

@SpringBootApplication
class SpringdrasilApplication

fun main(args: Array<String>) {
	runApplication<SpringdrasilApplication>(*args)
}

fun UUID.unhyphenatedString(): String {
	return this.toString().replace("-", "")
}
