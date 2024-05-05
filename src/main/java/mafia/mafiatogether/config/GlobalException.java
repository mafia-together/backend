package mafia.mafiatogether.config;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public GlobalException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
