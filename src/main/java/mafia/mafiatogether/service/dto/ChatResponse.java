package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import mafia.mafiatogether.domain.Message;
import mafia.mafiatogether.domain.job.JobType;

public record ChatResponse(
        String name,
        String contents,
        Timestamp timestamp,
        Boolean isOwner,
        JobType job
) {

    public static ChatResponse of(final Message message, final String name, final Boolean isMafia) {
        return new ChatResponse(
                message.getName(),
                message.getContents(),
                message.getTimestamp(),
                message.isOwner(name),
                message.getJob(isMafia, name)
        );
    }
}
