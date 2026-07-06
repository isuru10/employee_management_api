package com.ruhuna.employee_management_api.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Employee not found");
        ProblemDetail problemDetail = exceptionHandler.handleEntityNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("Resource Not Found", problemDetail.getTitle());
        assertEquals("Employee not found", problemDetail.getDetail());
        assertEquals(URI.create("about:blank"), problemDetail.getType());
    }

    @Test
    public void testHandleValidationException() {
        ValidationException exception = new ValidationException("Invalid skill description");
        ProblemDetail problemDetail = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Validation Error", problemDetail.getTitle());
        assertEquals("Invalid skill description", problemDetail.getDetail());
        assertEquals(URI.create("about:blank"), problemDetail.getType());
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("employee", "email", "must be a well-formed email address");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ProblemDetail problemDetail = exceptionHandler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("Validation Failed", problemDetail.getTitle());
        assertEquals("One or more validation constraints failed", problemDetail.getDetail());
        assertEquals(URI.create("about:blank"), problemDetail.getType());

        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) problemDetail.getProperties().get("errors");
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("email: must be a well-formed email address", errors.get(0));
    }
}
