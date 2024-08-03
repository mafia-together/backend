package mafia.mafiatogether.chat.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("chat")
@AllArgsConstructor
public class Chat {

    @Id
    private String code;
    private List<Message> messages;

    public Chat() {
        this.messages = new ArrayList<>();
    }

    public void saveMessage(Message message) {
        this.messages.add(message);
    }
}
