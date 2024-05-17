package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import java.util.List;
import mafia.mafiatogether.domain.status.EndStatus;

public record RoomResultResponse(
    String winnerJob,
    Timestamp endTime,
    List<RoomResultPlayerDto> winner,
    List<RoomResultPlayerDto> loser
) {
    public static RoomResultResponse of(
            final EndStatus endStatus
    ) {
        final List<RoomResultPlayerDto> winner = endStatus.getWinner()
                .stream()
                .map(RoomResultPlayerDto::of)
                .toList();

        final List<RoomResultPlayerDto> loser = endStatus.getLoser()
                .stream()
                .map(RoomResultPlayerDto::of)
                .toList();

        return new RoomResultResponse(
                endStatus.getWinnerJob().name(),
                endStatus.getEndTime(),
                winner,
                loser
        );
    }
}
