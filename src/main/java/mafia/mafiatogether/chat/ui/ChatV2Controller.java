package mafia.mafiatogether.chat.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.annotation.SendToChatWithRedis;
import mafia.mafiatogether.chat.application.ChatV2Service;
import mafia.mafiatogether.chat.application.dto.ChatV2Response;
import mafia.mafiatogether.chat.application.dto.request.ChatRequest;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.config.PlayerInfoDto;
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

    @MessageMapping("/chat/{code}/{name}")
    @SendToChatWithRedis("/sub/chat/{code}")
    public Message chat(
            @DestinationVariable("code") String code,
            @DestinationVariable("name") String name,
            @Payload ChatRequest request
    ) {
        return chatService.saveChat(name, code, request.content());
    }


}
