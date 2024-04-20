package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import mafia.mafiatogether.domain.Message;

public record ChatResponse(
        String name,
        String contents,
        Timestamp timestamp,
        boolean owner
) {

    public static ChatResponse of(final Message message, final String name) {
        return new ChatResponse(
                message.getName(),
                message.getContents(),
                message.getTimestamp(),
                message.isOwner(name)
        );
    }
}
