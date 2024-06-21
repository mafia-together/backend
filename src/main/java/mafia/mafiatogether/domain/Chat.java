package mafia.mafiatogether.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Chat {

    private List<Message> messages;

    public static Chat create() {
        return new Chat(new ArrayList<>());
    }

    public void save(final Message message) {
        messages.add(message);
    }

    public void clear() {
        messages.clear();
    }
}
