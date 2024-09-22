package mafia.mafiatogether.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Clock;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String name;
    private String contents;
    private long timestamp;

    public static Message of(String name, String content) {
        return new Message(name, content, Clock.systemDefaultZone().millis());
    }

}
