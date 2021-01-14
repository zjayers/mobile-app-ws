package io.ayers.mobileappws.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class AppExceptionsHandler {

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorMessage> handlePropertyValueException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ErrorMessage.builder()
                                               .message(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())
                                               .timestamp(new Date())
                                               .build());
    }

//    @ExceptionHandler(value = {Exception.class})
//    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .body(ErrorMessage.builder()
//                                               .message(exception.getMessage())
//                                               .timestamp(new Date())
//                                               .build());
//    }
}
