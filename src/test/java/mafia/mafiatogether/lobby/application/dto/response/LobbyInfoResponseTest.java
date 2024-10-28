package mafia.mafiatogether.lobby.application.dto.response;

import mafia.mafiatogether.lobby.domain.Lobby;
import mafia.mafiatogether.lobby.domain.LobbyInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class LobbyInfoResponseTest {

    @Test
    void 유저를_받아_로비_정보를_반환한다() {
        // given
        LobbyInfo lobbyInfo = LobbyInfo.of(3, 1, 1, 1);
        String roomCode = "code";
        String nameOfPlayer1 = "name1";
        String nameOfPlayer2 = "name2";
        Lobby lobby = Lobby.create(roomCode, lobbyInfo);
        lobby.joinPlayer(nameOfPlayer1);
        lobby.joinPlayer(nameOfPlayer2);

        // when
        LobbyInfoResponse actual = LobbyInfoResponse.of(lobby, nameOfPlayer1);

        // then
        assertThat(actual.isMaster()).isTrue();
        assertThat(actual.myName()).isEqualTo(nameOfPlayer1);
        assertThat(actual.totalPlayers()).isEqualTo(3);
        assertThat(actual.lobbyPlayerResponses())
                .extracting(LobbyPlayerResponse::name)
                .containsExactlyInAnyOrder(nameOfPlayer1, nameOfPlayer2);
    }

}