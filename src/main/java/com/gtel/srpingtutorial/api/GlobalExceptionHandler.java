package com.gtel.srpingtutorial.api;



import com.gtel.srpingtutorial.exception.ApplicationException;

import com.gtel.srpingtutorial.model.response.BadRequestResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;


@Log4j2
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    protected final HttpServletRequest httpServletRequest;




    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("ERROR: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(com.gtel.srpingtutorial.exception.ApplicationException.class)
    public ResponseEntity<BadRequestResponse> handleApplicationException(ApplicationException ex) {
        log.info("handleApplicationException {} with message {} , title {} , data {} ", ex.getCode(), ex.getMessage(), ex.getTitle(), ex.getData());

        BadRequestResponse responseData = new BadRequestResponse(ex, httpServletRequest);
        String traceId = UUID.randomUUID().toString();

        responseData.setRequestId(traceId);

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }
}
