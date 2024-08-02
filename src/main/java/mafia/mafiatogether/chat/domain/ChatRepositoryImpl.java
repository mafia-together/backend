package mafia.mafiatogether.chat.domain;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepositoryImpl implements ChatRepository {

    private final List<Chat> chats = new ArrayList<>();

    @Override
    public List<Chat> findByCode(String code) {
        return chats.stream()
                .filter(chat -> chat.getCode().equals(code))
                .toList();
    }

    @Override
    public Chat save(Chat chat) {
        chats.add(chat);
        return chat;
    }

    @Override
    public void deleteAllByCode(String code) {
        List<Chat> deletedChat = new ArrayList<>();
        for (Chat chat : chats) {
            if (chat.getCode().equals(code)) {
                deletedChat.add(chat);
            }
        }
        deletedChat.forEach(chat -> chats.remove(chat));
    }
}
