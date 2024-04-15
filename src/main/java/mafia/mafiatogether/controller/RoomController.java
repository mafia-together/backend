package mafia.mafiatogether.controller;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.RoomService;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomCreateResponse;
import mafia.mafiatogether.service.dto.RoomModifyRequest;
import mafia.mafiatogether.service.dto.RoomStatusResponse;
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
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomCreateResponse> create(@RequestBody RoomCreateRequest request) {
        return ResponseEntity.ok(roomService.create(request));
    }

    //TODO 방장 역할 추가
    //TODO GET -> POST
    @GetMapping
    public ResponseEntity<Void> join(
            @RequestParam("code") String code,
            @RequestParam("name") String name
    ) {
        roomService.join(code,name);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<RoomStatusResponse> findStatus(
            @PlayerInfo PlayerInfoDto playerInfoDto
    ) {
        return ResponseEntity.ok(roomService.findStatus(playerInfoDto.code()));
    }


    @PatchMapping("/status")
    public ResponseEntity<Void> modifyStatus(
            @PlayerInfo PlayerInfoDto playerInfoDto,
            @RequestBody RoomModifyRequest request
    ) {
        roomService.modifyStatus(playerInfoDto.code(), request);
        return ResponseEntity.ok().build();
    }
}
