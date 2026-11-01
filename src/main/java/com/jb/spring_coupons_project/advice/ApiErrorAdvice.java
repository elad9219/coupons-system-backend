
package com.jb.spring_coupons_project.advice;

import com.jb.spring_coupons_project.exception.CompanyException;
import com.jb.spring_coupons_project.exception.CustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.regex.Pattern;

@RestControllerAdvice
public class ApiErrorAdvice {

    private static final Pattern AMOUNT_FIELD =
            Pattern.compile("\\bamount\\b", Pattern.CASE_INSENSITIVE);

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
        String description = mentionsAmountField(exception)
                ? "Invalid coupon amount: enter a whole number " +
                "between 0 and 100000"
                : "Malformed request body";

        return new ErrorDetail(
                "Invalid request",
                description
        );
    }

    private boolean mentionsAmountField(Throwable exception) {
        Throwable current = exception;
        int depth = 0;

        while (current != null && depth < 12) {
            String message = current.getMessage();

            if (
                    message != null &&
                            AMOUNT_FIELD.matcher(message).find()
            ) {
                return true;
            }

            current = current.getCause();
            depth++;
        }

        return false;
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
