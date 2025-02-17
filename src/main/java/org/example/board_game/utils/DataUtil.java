package org.example.board_game.utils;

import java.util.Objects;

public final class DataUtil {
    public static <T> T getValueOrDefault(T value, T defaultValue) {
        return Objects.isNull(value) ? defaultValue : value;
    }
}
