package mafia.mafiatogether.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mafia.mafiatogether.chat.domain.vo.MessageType;

import java.time.Clock;
import java.util.Locale;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private static final String ENTER_MESSAGE_FORMAT = "%s님이 입장하셨습니다.";
    private static final String LEAVE_MESSAGE_FORMAT = "%s님이 퇴장하셨습니다.";

    private String name;
    private String contents;
    private MessageType messageType;
    private long timestamp;

    public static Message ofChat(String name, String content) {
        return new Message(
                name,
                content,
                MessageType.CHAT,
                Clock.systemDefaultZone().millis()
        );
    }

    public static Message fromEnter(String name) {
        return new Message(
                name,
                ENTER_MESSAGE_FORMAT.formatted(name),
                MessageType.ENTER,
                Clock.systemDefaultZone().millis()
        );
    }

    public static Message fromLeave(String name) {
        return new Message(
                name,
                LEAVE_MESSAGE_FORMAT.formatted(name),
                MessageType.LEAVE,
                Clock.systemDefaultZone().millis()
        );
    }

}
