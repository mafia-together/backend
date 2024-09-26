package mafia.mafiatogether.common.util;

import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;

import java.util.Base64;

@UtilityClass
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthExtractor {

    private static final int ALGORITHM_INDEX = 0;
    private static final int INFORMATION_INDEX = 1;
    private static final int INFORMATION_LENGTH = 2;
    private static final String SECURITY_ALGORITHM = "Basic";
    private static final int LENGTH_AFTER_SPLIT_BY_SECURITY_ALGORITHM = 2;

    public static String[] extractByAuthorization(String authorization) {
        String[] token = authorization.split(" ");

        if (token.length != LENGTH_AFTER_SPLIT_BY_SECURITY_ALGORITHM
                || !token[ALGORITHM_INDEX].equals(SECURITY_ALGORITHM)) {
            throw new AuthException(ExceptionCode.INVALID_AUTHENTICATION_FORM);
        }

        return decodeByBase64(token[INFORMATION_INDEX]);
    }

    public static String[] extractByCode(String code) {
        return decodeByBase64(code);
    }

    private static String[] decodeByBase64(String code) {
        String[] information = new String(Base64.getDecoder().decode(code))
                .split(":");
        if (information.length != INFORMATION_LENGTH) {
            throw new AuthException(ExceptionCode.INVALID_AUTHENTICATION_FORM);
        }
        return information;
    }

}
