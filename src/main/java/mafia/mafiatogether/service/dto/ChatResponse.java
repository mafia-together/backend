package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Message;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public record ChatResponse(
        String name,
        String contents,
        Timestamp timestamp,
        boolean owner
) {
    public static ChatResponse byOfName(final Message message, final String name) {
        return new ChatResponse(
                message.getName(),
                message.getContents(),
                message.getTimestamp(),
                message.isOwner(name)
        );
    }
}
