package mafia.mafiatogether.game.application.dto.response;

import java.sql.Timestamp;
import java.util.List;
import mafia.mafiatogether.game.domain.Game;

public record GameResultResponse(
        String winnerJob,
        Timestamp endTime,
        List<GameResultPlayerDto> winner,
        List<GameResultPlayerDto> loser
) {
    public static GameResultResponse from(final Game game) {
        final List<GameResultPlayerDto> winner = game.getWinners()
                .stream()
                .map(GameResultPlayerDto::of)
                .toList();
        final List<GameResultPlayerDto> loser = game.getLosers()
                .stream()
                .map(GameResultPlayerDto::of)
                .toList();

        return new GameResultResponse(
                game.getWinnerJob(),
                game.getStatus().getEndTime(),
                winner,
                loser
        );
    }
}
