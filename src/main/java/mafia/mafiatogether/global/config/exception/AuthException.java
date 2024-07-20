package mafia.mafiatogether.global.config.exception;

public class AuthException extends GlobalException{
    public AuthException(final ExceptionCode code) {
        super(code.getCode(), code.getMessage());
    }
}
