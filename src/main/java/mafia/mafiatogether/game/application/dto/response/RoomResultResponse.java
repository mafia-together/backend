package mafia.mafiatogether.game.application.dto.response;

import java.sql.Timestamp;
import java.util.List;
import mafia.mafiatogether.game.domain.Game;

public record RoomResultResponse(
        String winnerJob,
        Timestamp endTime,
        List<RoomResultPlayerDto> winner,
        List<RoomResultPlayerDto> loser
) {
    public static RoomResultResponse from(final Game game) {
        final List<RoomResultPlayerDto> winner = game.getWinners()
                .stream()
                .map(RoomResultPlayerDto::of)
                .toList();
        final List<RoomResultPlayerDto> loser = game.getLosers()
                .stream()
                .map(RoomResultPlayerDto::of)
                .toList();

        return new RoomResultResponse(
                game.getWinnerJob(),
                game.getStatus().getEndTime(),
                winner,
                loser
        );
    }
}
