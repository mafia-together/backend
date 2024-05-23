package mafia.mafiatogether.config.exception;

public class PlayerException extends GlobalException{
    public PlayerException(final ExceptionCode code) {
        super(code.getCode(), code.getMessage());
    }
}
