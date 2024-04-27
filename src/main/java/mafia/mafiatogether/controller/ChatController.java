package mafia.mafiatogether.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.ChatService;
import mafia.mafiatogether.service.dto.ChatRequest;
import mafia.mafiatogether.service.dto.ChatResponse;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChat(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(chatService.getChat(playerInfoDto.code(), playerInfoDto.name()));
    }

    @PostMapping
    public ResponseEntity<Void> sendChat(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @RequestBody ChatRequest request
    ) {
        chatService.createChat(playerInfoDto.code(), playerInfoDto.name(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
