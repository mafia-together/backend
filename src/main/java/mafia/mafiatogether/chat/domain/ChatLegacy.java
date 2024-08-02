package mafia.mafiatogether.chat.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatLegacy {

    private final List<Message> messages;

    public static ChatLegacy chat() {
        return new ChatLegacy(new ArrayList<>());
    }

    public void save(final Message message) {
        messages.add(message);
    }

    public void clear(){
        messages.clear();
    }
}
