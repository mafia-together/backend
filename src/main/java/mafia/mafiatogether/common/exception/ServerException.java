package mafia.mafiatogether.common.exception;

public class ServerException extends GlobalException{
    public ServerException(final ExceptionCode exceptionCode) {
        super(exceptionCode.getCode(), exceptionCode.getMessage());
    }
}
