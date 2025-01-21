package mafia.mafiatogether.game.application.dto.event;

import mafia.mafiatogether.game.domain.PlayerCollection;

public record StartGameEvent(String code, PlayerCollection playerCollection) {

}
