package mafia.mafiatogether.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Chat;
import mafia.mafiatogether.domain.Message;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.service.dto.ChatRequest;
import mafia.mafiatogether.service.dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    public List<ChatResponse> findAllChat(final Room room, final String name) {
        final Chat chat = room.getChat();
        return chat.getMessages().stream()
                .map(message -> ChatResponse.of(message, name))
                .toList();
    }

    public void saveChat(final Room room, final String name, final ChatRequest chatRequest) {
        final Chat chat = room.getChat();
        final Player player = room.findPlayer(name);
        chat.save(Message.of(player, chatRequest.contents()));
    }
}
