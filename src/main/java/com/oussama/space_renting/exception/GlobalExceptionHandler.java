package com.oussama.space_renting.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AuthorizationDeniedException ex) {
        return ResponseEntity.status(403).body("Unauthorized");
    }

    /*
     * TODO: check by cases for every field that can throw this exception
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

        String cause = ex.getMostSpecificCause().getMessage();

        String field = null;
        // I NEED TO ADD EVERY FIELD THAT HAS A CONSTRAINT THAT CAN BE VIOLATED IN CREATION
        if ( cause.contains("email")) {
            field = "email";
        } else if (cause.contains("phone_number")) {
            field = "phone_number";
        }

        if (field == null) {
            return ResponseEntity.badRequest().body("Duplicate entry");
        }

        return ResponseEntity.badRequest().body("Duplicate entry: " + field);
    }


//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
//        return ResponseEntity.status(403).body("Access Denied: " + ex.getMessage());
//    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGeneric(Exception ex) {
//        return ResponseEntity.status(500).body("Internal error: " + ex.getMessage());
//    }
}
