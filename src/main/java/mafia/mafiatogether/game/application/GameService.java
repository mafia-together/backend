package mafia.mafiatogether.game.application;

import java.time.Clock;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.room.application.dto.request.RoomModifyRequest;
import mafia.mafiatogether.room.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.room.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.room.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomRepository;
import mafia.mafiatogether.game.domain.status.EndStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;
    private final GameRepository gameRepository;

    public RoomStatusResponse findStatus(final String code) {
        final Long now = Clock.systemDefaultZone().millis();
        final Game game = gameRepository.findById(code)
                .orElseThrow(()->new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return new RoomStatusResponse(game.getStatusType(now));
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        final Room room = roomRepository.findByCode(code);
        Game game = Game.create(room, Clock.systemDefaultZone().millis());
        gameRepository.save(game);
    }

    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Game game = gameRepository.findById(code)
                .orElseThrow(()->new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Player player = game.getPlayer(name);
        return RoomInfoResponse.of(game, player, game.isMaster(player));
    }

    public RoomResultResponse findResult(final String code) {
        final Game game = gameRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!game.isEnd()) {
            throw new RoomException(ExceptionCode.GAME_IS_NOT_FINISHED);
        }
        return RoomResultResponse.of((EndStatus) game.getStatus());
    }
}
