package mafia.mafiatogether.common.domain;

import java.io.Serial;
import java.io.Serializable;


public record Embed(
        String title,
        String description
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static Embed createWarningEmbed(String message) {
        return new Embed("# Warning 정보 \uD83D\uDEA8", message);
    }

    public static Embed createErrorEmbed(String message) {
        return new Embed("# Error 정보 \uD83D\uDEA8", message);
    }

}
