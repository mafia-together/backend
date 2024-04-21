package mafia.mafiatogether.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.Room;
import mafia.mafiatogether.service.ChatService;
import mafia.mafiatogether.service.dto.ChatRequest;
import mafia.mafiatogether.service.dto.ChatResponse;
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
    public ResponseEntity<List<ChatResponse>> findAllChat(
            @PlayerRoom final Room room,
            @PlayerInfo final String name
    ) {
        return ResponseEntity.ok(chatService.findAllChat(room, name));
    }

    @PostMapping
    public ResponseEntity<Void> saveChat(
            @PlayerRoom final Room room,
            @PlayerInfo final String name,
            @RequestBody final ChatRequest request
    ) {
        chatService.saveChat(room, name, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
