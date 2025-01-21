package mafia.mafiatogether.chat.application.dto.response;

import java.sql.Timestamp;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.chat.domain.vo.MessageType;


public record ChatResponse(
        String name,
        String content,
        MessageType messageType,
        Timestamp timestamp,
        Boolean isOwner
) {

    public static ChatResponse of(
            Message message,
            boolean isOwner
    ) {
        return new ChatResponse(
                message.getName(),
                message.getContent(),
                message.getMessageType(),
                new Timestamp(message.getTimestamp()),
                isOwner
        );
    }
}
