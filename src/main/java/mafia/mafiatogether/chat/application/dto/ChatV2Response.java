package mafia.mafiatogether.chat.application.dto;

import mafia.mafiatogether.chat.domain.Message;

import java.sql.Timestamp;


public record ChatV2Response(
        String name,
        String contents,
        Timestamp timestamp,
        Boolean isOwner
) {

    public static ChatV2Response of(
            Message message,
            boolean isOwner
    ) {
        return new ChatV2Response(
                message.getName(),
                message.getContents(),
                new Timestamp(message.getTimestamp()),
                isOwner
        );
    }
}
