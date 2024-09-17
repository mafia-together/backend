package mafia.mafiatogether.common;

import mafia.mafiatogether.config.exception.AuthException;
import mafia.mafiatogether.config.exception.ExceptionCode;

import java.util.Base64;

public class AuthExtractor {

    private static final int ALGORITHM_INDEX = 0;
    private static final int INFORMATION_INDEX = 1;
    private static final int INFORMATION_LENGTH = 2;
    private static final String SECURITY_ALGORITHM = "Basic";
    private static final int LENGTH_AFTER_SPLIT_BY_SECURITY_ALGORITHM = 2;

    public static String[] extractBy(String code) {
        String[] token = code.split(" ");

        if (token.length != LENGTH_AFTER_SPLIT_BY_SECURITY_ALGORITHM
                || !token[ALGORITHM_INDEX].equals(SECURITY_ALGORITHM)) {
            throw new AuthException(ExceptionCode.INVALID_AUTHENTICATION_FORM);
        }

        String[] information = new String(Base64.getDecoder().decode(token[INFORMATION_INDEX]))
                .split(":");

        if (information.length != INFORMATION_LENGTH) {
            throw new AuthException(ExceptionCode.INVALID_AUTHENTICATION_FORM);
        }
        return information;
    }


}
