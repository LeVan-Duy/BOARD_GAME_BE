package org.example.board_game.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RandomStringUtil {

    static final String alpha = "abcdefghijklmnopqrstuvwxyz"; // a-z
    static final String alphaUpperCase = alpha.toUpperCase(); // A-Z
    static final String digits = "0123456789"; // 0-9
    static final String specials = "~=+%^*/()[]{}/!@#$?|";
    static final String ALPHA_NUMERIC = alpha + alphaUpperCase + digits;
    static final String ALL = alpha + alphaUpperCase + digits + specials;

    static final Random generator = new Random();

    /**
     * Random string with a-zA-Z0-9, not included special characters
     */
    public static String randomAlphaNumeric(int numberOfCharacters) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfCharacters; i++) {
            int number = randomNumber(0, ALPHA_NUMERIC.length() - 1);
            char ch = ALPHA_NUMERIC.charAt(number);
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String generateRandomPassword(int length) {
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHA_NUMERIC.length());
            password.append(ALPHA_NUMERIC.charAt(index));
        }
        return password.toString();
    }

    public static int randomNumber(int min, int max) {
        return generator.nextInt((max - min) + 1) + min;
    }

}
