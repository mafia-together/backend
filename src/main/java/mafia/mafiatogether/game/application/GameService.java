package mafia.mafiatogether.game.application;

import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.game.application.dto.request.RoomModifyRequest;
import mafia.mafiatogether.game.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.game.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.game.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.Player;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.room.domain.Room;
import mafia.mafiatogether.room.domain.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GameService {

    private final RoomRepository roomRepository;
    private final GameRepository gameRepository;

    public RoomStatusResponse findStatus(final String code) {
        final Optional<Game> game = gameRepository.findById(code);
        if (game.isPresent()) {
            return new RoomStatusResponse(checkStatusChanged(game.get()));
        }
        if (roomRepository.existsById(code)) {
            return new RoomStatusResponse(StatusType.WAIT);
        }
        throw new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE);
    }

    private StatusType checkStatusChanged(final Game game) {
        game.setStatsSnapshot();
        final StatusType statusType = game.getStatusType(Clock.systemDefaultZone().millis());
        if (game.isStatusChanged()) {
            gameRepository.save(game);
        }
        return statusType;
    }

    public void modifyStatus(final String code, final RoomModifyRequest request) {
        final Room room = roomRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        Game game = Game.create(room, Clock.systemDefaultZone().millis());
        game.distributeRole();
        gameRepository.save(game);
    }

    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Game game = gameRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Player player = game.getPlayer(name);
        return RoomInfoResponse.of(game, player, game.isMaster(player));
    }

    public RoomResultResponse findResult(final String code) {
        final Game game = gameRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!game.isEnd()) {
            throw new RoomException(ExceptionCode.GAME_IS_NOT_FINISHED);
        }
        return RoomResultResponse.from(game);
    }
}
