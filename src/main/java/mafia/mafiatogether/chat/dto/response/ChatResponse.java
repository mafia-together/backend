package mafia.mafiatogether.chat.dto.response;

import java.sql.Timestamp;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.job.domain.JobType;

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
