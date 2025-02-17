package org.example.board_game.infrastructure.exception;

import org.example.board_game.utils.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handlerInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ExceptionHandler(value = {WebExchangeBindException.class})
    protected ResponseEntity<Response> handleWebExchangeBindException(WebExchangeBindException ex) {
        Response data = Response.fail(ex.getAllErrors().stream().findFirst().get().getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.ok().body(data);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<Response> handleConstraintViolation(ConstraintViolationException ex) {
        Response data = Response.fail(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.ok().body(data);
    }

    @ExceptionHandler(value = {ServerWebInputException.class})
    protected ResponseEntity<Response> handleServerWebInput(ServerWebInputException ex) {
        Response data = Response.fail("Dữ liệu đầu vào không hợp lệ.", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.ok().body(data);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Response> handleException(Exception ex) {
        Response data = Response.fail("Gặp lỗi trong quá trình xử lý.", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.ok().body(data);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<Response> handleNotFoundException(ResourceNotFoundException ex) {
        Response data = Response.fail(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.ok().body(data);
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    protected ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Không có quyền truy cập";
        Response data = Response.fail(message, HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(data);
    }

}
