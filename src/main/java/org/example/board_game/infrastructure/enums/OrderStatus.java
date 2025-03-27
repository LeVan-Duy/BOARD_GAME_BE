package org.example.board_game.infrastructure.enums;

public enum OrderStatus {
    PENDING("Đơn hàng đang được chuẩn bị."),
    WAIT_FOR_CONFIRMATION("Đơn hàng đang chờ xác nhận."),
    WAIT_FOR_DELIVERY("Đơn hàng sẵn sàng để giao."),
    DELIVERING("Đơn hàng đang được giao."),
    COMPLETED("Đơn hàng đã hoàn thành."),
    CANCELED("Đơn hàng đã bị hủy.");

    public final String name;

    private OrderStatus(String name) {
        this.name = name;
    }
}
