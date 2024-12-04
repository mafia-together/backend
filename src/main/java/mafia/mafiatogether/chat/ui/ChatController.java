package mafia.mafiatogether.chat.ui;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.annotation.SendToChatWithRedis;
import mafia.mafiatogether.chat.application.ChatService;
import mafia.mafiatogether.chat.application.dto.request.ChatRequest;
import mafia.mafiatogether.chat.application.dto.response.ChatResponse;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat")
    public ResponseEntity<List<ChatResponse>> findAllChat(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(chatService.findAllChat(playerInfoDto.code(), playerInfoDto.name()));
    }

    @MessageMapping("/chat/{code}/enter")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message enterChat(
            @DestinationVariable("code") String code,
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return chatService.enter(playerInfoDto.name(), code);
    }

    @MessageMapping("/chat/{code}")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message createChat(
            @DestinationVariable("code") String code,
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @Payload ChatRequest request
    ) {
        return chatService.chat(playerInfoDto.name(), code, request.content());
    }

    @MessageMapping("/chat/{code}/leave")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message leaveChat(
            @DestinationVariable("code") String code,
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return chatService.leave(playerInfoDto.name(), code);
    }

}
