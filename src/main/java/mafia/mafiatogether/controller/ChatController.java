package mafia.mafiatogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.ChatService;
import mafia.mafiatogether.service.dto.ChatRequest;
import mafia.mafiatogether.service.dto.ChatResponse;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import org.springframework.http.HttpHeaders;
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
@Tag(name = "02. Chat API", description = "채팅 조회 , 전송 API")
public class ChatController {

    private final ChatService chatService;


    @GetMapping
    @Operation(
            description = "채팅 조회 API",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    public ResponseEntity<List<ChatResponse>> findAllChat(@PlayerInfo PlayerInfoDto playerInfoDto) {
        return ResponseEntity.ok(chatService.findAllChat(playerInfoDto.code(), playerInfoDto.name()));
    }


    @PostMapping
    @Operation(
            description = "채팅 전송 API",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    public ResponseEntity<Void> saveChat(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @RequestBody ChatRequest request
    ) {
        chatService.saveChat(playerInfoDto.code(), playerInfoDto.name(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
