package mafia.mafiatogether.chat.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.annotation.SendToChatWithRedis;
import mafia.mafiatogether.chat.application.ChatV2Service;
import mafia.mafiatogether.chat.application.dto.ChatV2Response;
import mafia.mafiatogether.chat.application.dto.request.ChatRequest;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatV2Controller {

    private final ChatV2Service chatService;

    @GetMapping("/v2/chat")
    public ResponseEntity<List<ChatV2Response>> findAllChat(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(chatService.findAllChat(playerInfoDto.code(), playerInfoDto.name()));
    }

    @MessageMapping("/chat/enter/{code}/{name}")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message enterChat(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name
    ) {
        return chatService.enter(name, code);
    }

    @MessageMapping("/chat/leave/{code}/{name}")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message leaveChat(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name
    ) {
        return chatService.leave(name, code);
    }

    @MessageMapping("/chat/{code}/{name}")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message createChat(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name,
            @Payload ChatRequest request
    ) {
        return chatService.chat(name, code, request.content());
    }


}
