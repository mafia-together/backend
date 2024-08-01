package mafia.mafiatogether.game.ui;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.PlayerInfo;
import mafia.mafiatogether.job.application.dto.PlayerInfoDto;
import mafia.mafiatogether.room.application.RoomService;
import mafia.mafiatogether.room.application.dto.request.RoomModifyRequest;
import mafia.mafiatogether.room.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.room.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomStatusResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final RoomService roomService;

    @GetMapping("/rooms/status")
    public ResponseEntity<RoomStatusResponse> findStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findStatus(playerInfoDto.code()));
    }

    @PatchMapping("/rooms/status")
    public ResponseEntity<Void> modifyStatus(
            @PlayerInfo final PlayerInfoDto playerInfoDto,
            @RequestBody final RoomModifyRequest request
    ) {
        roomService.modifyStatus(playerInfoDto.code(), request);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/rooms/result")
    public ResponseEntity<RoomResultResponse> findResult(
            @PlayerInfo final PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findResult(playerInfoDto.code()));
    }

    @GetMapping("/rooms/info")
    public ResponseEntity<RoomInfoResponse> findRoomInfo(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findRoomInfo(playerInfoDto.code(), playerInfoDto.name()));
    }
}
