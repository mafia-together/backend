package mafia.mafiatogether.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record DiscordMessage(
        String content,
        List<Embed> embeds
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static DiscordMessage createWarningDiscordMessage(String message) {
        return new DiscordMessage(
                "# Warning이 발생했어요.. 파워 엉덩이 가져와",
                List.of(Embed.createWarningEmbed(message))
        );
    }

    public static DiscordMessage createErrorDiscordMessage(String message) {
        return new DiscordMessage(
                "# Error가 발생했어요!!!!!!!!!!!!!! 달리 엉덩이 가져와 매 맞게",
                List.of(Embed.createErrorEmbed(message))
        );
    }

}
