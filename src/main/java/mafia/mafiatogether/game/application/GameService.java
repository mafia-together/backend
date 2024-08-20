package mafia.mafiatogether.game.application;

import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.GameException;
import mafia.mafiatogether.game.application.dto.response.RoomInfoResponse;
import mafia.mafiatogether.game.application.dto.response.RoomResultResponse;
import mafia.mafiatogether.game.application.dto.response.RoomStatusResponse;
import mafia.mafiatogether.game.domain.Game;
import mafia.mafiatogether.game.domain.GameRepository;
import mafia.mafiatogether.game.domain.status.StatusType;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;

    @Transactional
    public RoomStatusResponse findStatus(final String code) {
        final Optional<Game> game = gameRepository.findById(code);
        if (game.isPresent()) {
            return new RoomStatusResponse(checkStatusChanged(game.get()));
        }
        if (lobbyRepository.existsById(code)) {
            return new RoomStatusResponse(StatusType.WAIT);
        }
        throw new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE);
    }

    private StatusType checkStatusChanged(final Game game) {
        game.setStatsSnapshot();
        final StatusType statusType = game.getStatusType(Clock.systemDefaultZone().millis());
        if (game.isDeleted()){
            gameRepository.delete(game);
            return StatusType.WAIT;
        }
        if (game.isStatusChanged()) {
            gameRepository.save(game);
        }
        return statusType;
    }

    @Transactional
    public void modifyStatus(final String code) {
        final Lobby lobby = lobbyRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        Game game = Game.create(lobby, Clock.systemDefaultZone().millis());
        game.distributeRole();
        gameRepository.save(game);
    }

    @Transactional(readOnly = true)
    public RoomInfoResponse findRoomInfo(final String code, final String name) {
        final Optional<Game> game = gameRepository.findById(code);
        if (!game.isPresent()) {
            return getLobbyInfo(code, name);
        }
        return RoomInfoResponse.ofGame(game.get(), name);
    }

    private RoomInfoResponse getLobbyInfo(final String code, final String name) {
        final Lobby lobby = lobbyRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        return RoomInfoResponse.ofRoom(lobby, name);
    }

    @Transactional(readOnly = true)
    public RoomResultResponse findResult(final String code) {
        final Game game = gameRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        if (!game.isEnd()) {
            throw new GameException(ExceptionCode.GAME_IS_NOT_FINISHED);
        }
        return RoomResultResponse.from(game);
    }
}
