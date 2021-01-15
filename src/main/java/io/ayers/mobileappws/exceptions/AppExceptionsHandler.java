package io.ayers.mobileappws.exceptions;

import io.ayers.mobileappws.constants.ErrorMessageConstants;
import io.ayers.mobileappws.models.responses.ErrorMessageResponseModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class AppExceptionsHandler {

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    public ResponseEntity<ErrorMessageResponseModel> handlePropertyValueException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ErrorMessageResponseModel.builder()
                                                            .message(ErrorMessageConstants.MISSING_REQUIRED_FIELD.getErrorMessage())
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
