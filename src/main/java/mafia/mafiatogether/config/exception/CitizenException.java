package mafia.mafiatogether.config.exception;

public class CitizenException extends GlobalException{
    public CitizenException(final ExceptionCode code) {
        super(code.getCode(), code.getMessage());
    }
}
