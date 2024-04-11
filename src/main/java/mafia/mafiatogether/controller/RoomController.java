package mafia.mafiatogether.controller;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.service.RoomService;
import mafia.mafiatogether.service.dto.CreateRoomRequest;
import mafia.mafiatogether.service.dto.CreateRoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<CreateRoomResponse> create(@RequestBody CreateRoomRequest request) {
        return ResponseEntity.ok(roomService.create(request));
    }
}
