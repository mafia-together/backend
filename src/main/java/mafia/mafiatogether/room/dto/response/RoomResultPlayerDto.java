package mafia.mafiatogether.room.dto.response;

import mafia.mafiatogether.job.domain.Player;

public record RoomResultPlayerDto(
        String name,
        Boolean isAlive,
        String job
) {
    public static RoomResultPlayerDto of(
            final Player player
    ) {
        return new RoomResultPlayerDto(
                player.getName(),
                player.isAlive(),
                player.getJobType().name()
        );
    }
}
