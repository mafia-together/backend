package mafia.mafiatogether.common.exception;

public class GameException extends GlobalException{

    public GameException(final ExceptionCode code) {
        super(code.getCode(),code.getMessage());
    }
}
