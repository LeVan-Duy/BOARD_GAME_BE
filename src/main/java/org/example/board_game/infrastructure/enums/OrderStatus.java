package org.example.board_game.infrastructure.enums;

public enum OrderStatus {
    PENDING("Preparing order"),
    WAIT_FOR_CONFIRMATION("Order is waiting for confirmation"),
    WAIT_FOR_DELIVERY("Order is ready"),
    DELIVERING("Order is being delivered"),
    COMPLETED("Order completed"),
    CANCELED("Order is cancelled");

    public final String name;

    private OrderStatus(String name) {
        this.name = name;
    }
}
