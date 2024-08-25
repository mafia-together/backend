package mafia.mafiatogether.lobby.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.GameException;
import mafia.mafiatogether.lobby.application.dto.request.LobbyCreateRequest;
import mafia.mafiatogether.lobby.application.dto.response.LobbyCodeResponse;
import mafia.mafiatogether.lobby.application.dto.response.LobbyValidateResponse;
import mafia.mafiatogether.lobby.domain.CodeGenerator;
import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import mafia.mafiatogether.lobby.domain.LobbyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LobbyService {

    private final LobbyRepository lobbyRepository;

    @Transactional
    public LobbyCodeResponse create(final LobbyCreateRequest request) {
        String code = CodeGenerator.generate();
        while (lobbyRepository.existsById(code)){
            code = CodeGenerator.generate();
        }
        final LobbyInfo lobbyInfo = LobbyInfo.of(request.total(), request.mafia(), request.doctor(), request.police());
        final Lobby lobby = Lobby.create(code, lobbyInfo);
        lobbyRepository.save(lobby);
        return new LobbyCodeResponse(code);
    }

    @Transactional
    public void join(final String code, final String name) {
        final Lobby lobby = lobbyRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        lobby.joinPlayer(name);
        lobbyRepository.save(lobby);
    }

    @Transactional(readOnly = true)
    public LobbyValidateResponse validateCode(final String code) {
        return new LobbyValidateResponse(lobbyRepository.existsById(code));
    }
}
