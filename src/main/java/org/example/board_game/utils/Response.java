package org.example.board_game.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class Response<T> implements Serializable {

    private T data;
    private boolean success;
    private int code;
    private String message;
    private String description;
    private Long timestamp;

    @JsonIgnore
    private RuntimeException exception;

    public Response() {
        this.success = true;
        this.code = 0;
        this.message = "Default message";
        this.description = "Message is init response";
        this.timestamp = Instant.now().toEpochMilli();
    }

    public Response(T map, T status) {
    }

    public static <T> Response<T> of(T res) {
        Response<T> response = new Response<>();
        response.data = res;
        response.success();
        return response;
    }

    public static <T> Response<T> ok() {
        Response<T> response = new Response<>();
        response.success();
        return response;
    }

    public static <T> Response<T> ok(String message) {
        Response<T> response = new Response<>();
        response.message = message;
        response.success();
        return response;
    }

    public static <T> Response<T> fail(RuntimeException exception) {
        Response<T> response = new Response<>();
        response.success = false;
        response.exception = exception;
        return response;
    }


    public Response<T> success() {
        this.success = true;
        this.code = 0;
        return this;
    }

    public Response<T> success(String message) {
        this.success = true;
        this.code = 0;
        this.message = message;
        return this;
    }

    public Response<T> success(String message, int statusCode) {
        this.success = true;
        this.code = statusCode;
        this.message = message;
        return this;
    }

    public Response<T> success(String message, String description) {
        this.success = true;
        this.code = 0;
        this.message = message;
        this.description = description;
        return this;
    }


    public Response<T> failWithData(String message, int statusCode) {
        this.success = false;
        this.code = statusCode;
        this.message = message;
        return this;
    }

    public static <T> Response<T> fail(String message, int statusCode) {
        Response<T> response = new Response<>();
        response.success = false;
        response.code = statusCode;
        response.message = message;
        return response;
    }

    public T getData() {
        if (this.exception != null) {
            throw this.exception;
        } else {
            return this.data;
        }
    }

    public boolean isSuccess() {
        if (this.exception != null) {
            throw this.exception;
        } else {
            return this.success;
        }
    }

    public String toString() {
        String var10000 = String.valueOf(this.data);
        return "Response {,data=" + var10000 + ",code=" + this.code + ",message=" + this.message + ",description=" + this.description + ",timestamp=" + this.timestamp + ",exception=" + String.valueOf(this.exception) + "}";
    }
}
