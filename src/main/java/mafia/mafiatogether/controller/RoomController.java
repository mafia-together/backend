package mafia.mafiatogether.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.RoomService;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import mafia.mafiatogether.service.dto.RoomAuthResponse;
import mafia.mafiatogether.service.dto.RoomCodeResponse;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
@Tag(name = "01. Room API", description = "방 생성, 참가, 상태 조회, 코드 조회 API")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(description = "방 생성 API")
    public ResponseEntity<RoomCodeResponse> create(@RequestBody final RoomCreateRequest request) {
        return ResponseEntity.ok(roomService.create(request));
    }

    @GetMapping
    @Operation(description = "방 참가 API")
    public ResponseEntity<RoomAuthResponse> join(
            @RequestParam("code") final String code,
            @RequestParam("name") final String name
    ) {
        roomService.join(code, name);
        String encodedStr = Base64.getEncoder().encodeToString((code + ":" + name).getBytes());
        return ResponseEntity.ok(new RoomAuthResponse(encodedStr));
    }

    @GetMapping("/status")
    @Operation(
            description = "방 상태 조회 API",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    public ResponseEntity<RoomStatusResponse> findStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findStatus(playerInfoDto.code()));
    }

    @PatchMapping("/status")
    @Operation(
            description = "방 상태 변경 API",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    public ResponseEntity<Void> modifyStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto,
            @RequestBody final RoomModifyRequest request
    ) {
        roomService.modifyStatus(playerInfoDto.code(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/code")
    @Operation(
            description = "방 코드 조회 API",
            security = {@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)}
    )
    public ResponseEntity<RoomCodeResponse> findCode(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(new RoomCodeResponse(playerInfoDto.code()));
    }
}
