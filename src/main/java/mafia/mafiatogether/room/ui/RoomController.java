package mafia.mafiatogether.room.ui;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.room.application.RoomService;
import mafia.mafiatogether.job.application.dto.PlayerInfoDto;
import mafia.mafiatogether.room.application.dto.response.RoomAuthResponse;
import mafia.mafiatogether.room.application.dto.response.RoomCodeResponse;
import mafia.mafiatogether.room.application.dto.request.RoomCreateRequest;
import mafia.mafiatogether.room.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.room.application.dto.request.RoomModifyRequest;
import mafia.mafiatogether.room.application.dto.response.RoomNightResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.room.application.dto.response.RoomValidateResponse;
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
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomCodeResponse> create(@RequestBody final RoomCreateRequest request) {
        return ResponseEntity.ok(roomService.create(request));
    }

    @GetMapping
    public ResponseEntity<RoomAuthResponse> join(
            @RequestParam("code") final String code,
            @RequestParam("name") final String name
    ) {
        roomService.join(code, name);
        String encodedStr = Base64.getEncoder().encodeToString((code + ":" + name).getBytes());
        return ResponseEntity.ok(new RoomAuthResponse(encodedStr));
    }

    @GetMapping("/status")
    public ResponseEntity<RoomStatusResponse> findStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findStatus(playerInfoDto.code()));
    }

    @PatchMapping("/status")
    public ResponseEntity<Void> modifyStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto,
            @RequestBody final RoomModifyRequest request
    ) {
        roomService.modifyStatus(playerInfoDto.code(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/code")
    public ResponseEntity<RoomCodeResponse> findCode(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(new RoomCodeResponse(playerInfoDto.code()));
    }


    @GetMapping("/code/exist")
    public ResponseEntity<RoomValidateResponse> validateCode(
            @RequestParam("code") final String code
    ) {
        return ResponseEntity.ok(roomService.validateCode(code));
    }

    @GetMapping("/info")
    public ResponseEntity<RoomInfoResponse> findRoomInfo(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findRoomInfo(playerInfoDto.code(), playerInfoDto.name()));
    }

    @GetMapping("/result")
    public ResponseEntity<RoomResultResponse> findResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findResult(playerInfoDto.code()));
    }

    @GetMapping("/night/result")
    public ResponseEntity<RoomNightResultResponse> findNightResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findNightResult(playerInfoDto.code()));
    }
}
