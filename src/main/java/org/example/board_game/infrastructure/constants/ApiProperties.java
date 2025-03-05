package org.example.board_game.infrastructure.constants;

public final class ApiProperties {

    private ApiProperties() {
    }
    public static final String GET_ALL = "/get-all";
    public static final String GET_LIST = "/list";
    public static final String SAVE = "/save";
    public static final String CREATE = "/create";
    public static final String UPDATE = "/update/{guid}";
    public static final String DELETE = "/delete/{guid}";
    public static final String SELECT = "/select";
    public static final String IMPORT_EXCEL = "/import-excel";
    public static final String URL_RESET_PASSWORD_LOCAL = "http://localhost:5173/reset-password?token=";

}
