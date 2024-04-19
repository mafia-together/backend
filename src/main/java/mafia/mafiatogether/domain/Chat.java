package mafia.mafiatogether.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Chat {

    private final List<Message> messages;

    public static Chat chat() {
        return new Chat(new ArrayList<>());
    }

    public void save(final Message message) {
        messages.add(message);
    }
}
