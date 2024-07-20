package mafia.mafiatogether.global.config.exception;

public class RoomException extends GlobalException{

    public RoomException(final ExceptionCode code) {
        super(code.getCode(),code.getMessage());
    }
}
