package mafia.mafiatogether.chat.ui;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.chat.application.ChatService;
import mafia.mafiatogether.chat.application.dto.request.ChatRequest;
import mafia.mafiatogether.chat.application.dto.response.ChatResponse;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
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
    public ResponseEntity<List<ChatResponse>> findAllChat(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(chatService.findAllChat(playerInfoDto.code(), playerInfoDto.name()));
    }

    @PostMapping
    public ResponseEntity<Void> saveChat(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @Valid @RequestBody ChatRequest request
    ) {
        chatService.saveChat(playerInfoDto.code(), playerInfoDto.name(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
