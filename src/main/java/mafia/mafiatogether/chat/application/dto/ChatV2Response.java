package mafia.mafiatogether.chat.application.dto;

import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.chat.domain.vo.MessageType;

import java.sql.Timestamp;


public record ChatV2Response(
        String name,
        String contents,
        MessageType messageType,
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
                message.getMessageType(),
                new Timestamp(message.getTimestamp()),
                isOwner
        );
    }
}
