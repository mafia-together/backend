package mafia.mafiatogether.common.resolver;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.util.AuthExtractor;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthResolver {

    public String[] resolve(final String authorization) {
        if (authorization == null) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }
        if (!authorization.startsWith("Basic")) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }

        return AuthExtractor.extractByAuthorization(authorization);
    }
}
