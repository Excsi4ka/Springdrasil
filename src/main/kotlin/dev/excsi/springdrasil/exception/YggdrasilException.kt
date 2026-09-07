package dev.excsi.springdrasil.exception

import org.springframework.http.HttpStatus

class YggdrasilException(
    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    val error: String = "Undefined",
    val errorMessage: String = "Undefined",
) : RuntimeException()