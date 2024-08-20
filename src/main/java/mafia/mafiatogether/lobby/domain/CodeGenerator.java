package mafia.mafiatogether.lobby.domain;


import java.util.Random;

public class CodeGenerator {

    private final static int LEFT_LIMIT = 48;
    private final static int RIGHT_LIMIT = 122;
    private final static int TARGET_STRING_LENGTH = 10;

    public static String generate() {
        Random random = new Random();

        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
