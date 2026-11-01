
package com.jb.spring_coupons_project.advice;

import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ApiErrorAdvice {

    @ExceptionHandler({
            CompanyException.class,
            CustomerException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetail handleBusinessValidation(Exception exception) {
        return new ErrorDetail(
                "Invalid request",
                exception.getMessage()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetail handleMalformedBody(
            HttpMessageNotReadableException exception
    ) {
        return new ErrorDetail(
                "Invalid request",
                "Malformed request body"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetail handleInvalidParameter(
            MethodArgumentTypeMismatchException exception
    ) {
        return new ErrorDetail(
                "Invalid request",
                "Invalid request parameter"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetail handleInvalidFields(
            MethodArgumentNotValidException exception
    ) {
        return new ErrorDetail(
                "Invalid request",
                "Request validation failed"
        );
    }
}
