package mafia.mafiatogether.chat.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.job.domain.Player;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.repository.RoomManager;
import mafia.mafiatogether.chat.dto.request.ChatRequest;
import mafia.mafiatogether.chat.dto.response.ChatResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RoomManager roomManager;

    public List<ChatResponse> findAllChat(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Chat chat = room.getChat();
        final Player player = room.getPlayer(name);
        return chat.getMessages().stream()
                .map(message -> ChatResponse.of(message, name, player.isMafia()))
                .toList();
    }

    public void saveChat(final String code, final String name, final ChatRequest chatRequest) {
        final Room room = roomManager.findByCode(code);
        final Chat chat = room.getChat();
        final Player player = room.getPlayer(name);
        chat.save(Message.of(player, chatRequest.contents()));
    }
}
