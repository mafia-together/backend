package mafia.mafiatogether.global.config.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final int codes;
    private final String message;

    public GlobalException(final int code, final String message) {
        super(message);
        this.codes = code;
        this.message = message;
    }
}
