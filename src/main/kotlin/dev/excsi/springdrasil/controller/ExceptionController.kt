package dev.excsi.springdrasil.controller

import dev.excsi.springdrasil.dto.YggdrasilErrorResponse
import dev.excsi.springdrasil.exception.YggdrasilException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionController {

    @ExceptionHandler(YggdrasilException::class)
    fun handleYggdrasilException(ex: YggdrasilException): ResponseEntity<YggdrasilErrorResponse> {
        return ResponseEntity
            .status(ex.httpStatus)
            .body(YggdrasilErrorResponse(ex.error, ex.errorMessage))
    }
}