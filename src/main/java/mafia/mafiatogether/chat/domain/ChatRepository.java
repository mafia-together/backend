package mafia.mafiatogether.chat.domain;

import java.util.List;

public interface ChatRepository {
    List<Chat> findByCode(String code);

    Chat save(Chat chat);

    void deleteAllByCode(String code);
}
