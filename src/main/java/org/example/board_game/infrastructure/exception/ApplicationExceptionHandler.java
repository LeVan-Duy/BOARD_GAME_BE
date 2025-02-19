package org.example.board_game.infrastructure.exception;

import org.example.board_game.utils.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import java.nio.file.AccessDeniedException;


@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = {WebExchangeBindException.class})
    protected ResponseEntity<Response> handleWebExchangeBindException(WebExchangeBindException ex) {
        Response data = Response.fail(ex.getAllErrors().stream().findFirst().get().getDefaultMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.ok().body(data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationException(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
        Response response = Response.fail(error, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(response);
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
