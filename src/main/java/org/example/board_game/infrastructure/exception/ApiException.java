package org.example.board_game.infrastructure.exception;

public class ApiException extends RuntimeException{
    public ApiException(String message) {
        super(message);
    }
}
