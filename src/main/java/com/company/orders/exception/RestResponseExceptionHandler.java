package com.company.orders.exception;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.company.orders.model.ErrorResponse;

@ControllerAdvice
public class RestResponseExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseExceptionHandler.class);

	@ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBadRequestError(MethodArgumentNotValidException ex)
    {
        LOGGER.error("Bad request: ", ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        fieldErrors.forEach(fieldError -> {
        	sb.append(fieldError.getField());
        	sb.append(": ");
        	sb.append(fieldError.getDefaultMessage());
        	sb.append(";");
        });
        return new ResponseEntity<>(new ErrorResponse(sb.toString()), null, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({ EntityNotFoundException.class })
	@ResponseBody
    public ResponseEntity<ErrorResponse> handleNotFoundError(EntityNotFoundException ex)
    {
        LOGGER.error("Entity not found: ", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), null, HttpStatus.NO_CONTENT);
    }
	
	@ExceptionHandler({ OrderTakenException.class })
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleConflictError(Exception ex)
    {
        LOGGER.error("Conflict: ", ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), null, HttpStatus.CONFLICT);
    }
	
	@ExceptionHandler({ MethodArgumentTypeMismatchException.class, MissingPathVariableException.class, MissingServletRequestParameterException.class })
    @ResponseBody
    public ResponseEntity<ErrorResponse> badRequestError(Exception ex)
    {
        LOGGER.error("Bad request: ", ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), null, HttpStatus.BAD_REQUEST);
    }
	
	@ExceptionHandler({ IOException.class, RestClientException.class, GoogleApiClientException.class, Exception.class })
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleInternalError(Exception ex)
    {
        LOGGER.error("Internal Server Error: ", ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
}
