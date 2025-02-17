package org.example.board_game.infrastructure.exception;

public class AccessException extends RuntimeException{
    public AccessException(String message) {
        super(message);
    }
}
