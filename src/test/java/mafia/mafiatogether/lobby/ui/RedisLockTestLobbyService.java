package mafia.mafiatogether.lobby.ui;

import mafia.mafiatogether.common.annotation.RedisLock;
import mafia.mafiatogether.common.annotation.RedisLockTarget;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.GameException;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
class RedisLockTestLobbyService {

    private final LobbyRepository lobbyRepository;

    protected RedisLockTestLobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    @RedisLock(key = "lobby")
    protected void waitAndInput(@RedisLockTarget String code, String name) throws InterruptedException {
        Thread.sleep(50);
        final Lobby lobby = lobbyRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        lobby.joinPlayer(name);
        lobbyRepository.save(lobby);
    }
}
