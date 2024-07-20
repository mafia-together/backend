package mafia.mafiatogether.global.config.exception;

public class PlayerException extends GlobalException{
    public PlayerException(final ExceptionCode code) {
        super(code.getCode(), code.getMessage());
    }
}
