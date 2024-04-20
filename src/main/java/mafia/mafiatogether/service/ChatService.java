package mafia.mafiatogether.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Chat;
import mafia.mafiatogether.domain.Message;
import mafia.mafiatogether.domain.Player;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.domain.RoomManager;
import mafia.mafiatogether.service.dto.ChatRequest;
import mafia.mafiatogether.service.dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final RoomManager roomManager;

    public List<ChatResponse> getChat(final String code, final String name) {
        final Room room = roomManager.findByCode(code);
        final Chat chat = room.getChat();
        return chat.getMessages().stream()
                .map(message ->
                        new ChatResponse(
                                message.getName(),
                                message.getContents(),
                                message.getTimestamp(),
                                message.isOwner(name)
                        )
                ).toList();
    }

    public void createChat(final String code, final String name, final ChatRequest chatRequest){
        final Room room = roomManager.findByCode(code);
        final Chat chat = room.getChat();
        final Player player = room.findPlayer(name);
        chat.save(Message.of(player,chatRequest.contents()));
    }
}
