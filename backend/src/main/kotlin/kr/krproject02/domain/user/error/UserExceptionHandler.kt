package kr.krproject02.domain.user.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["kr.krproject02.domain.user"])
class UserExceptionHandler {

    @ExceptionHandler(UserException::class)
    fun handleUserException(exception: UserException): ResponseEntity<UserErrorResponse> {
        val errorCode = exception.errorCode
        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(UserErrorResponse(errorCode.code, errorCode.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException): ResponseEntity<UserErrorResponse> {
        val fieldError: FieldError? = exception.bindingResult.fieldError
        val errorCode = if (fieldError?.field == "password") {
            UserErrorCode.INVALID_PASSWORD
        } else {
            UserErrorCode.INVALID_BIRTH_DATE
        }
        return ResponseEntity
            .status(errorCode.httpStatus)
            .body(UserErrorResponse(errorCode.code, errorCode.message))
    }
}

