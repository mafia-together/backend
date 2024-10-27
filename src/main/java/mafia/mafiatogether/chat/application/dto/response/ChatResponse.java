package mafia.mafiatogether.chat.application.dto.response;

import java.sql.Timestamp;

import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.job.domain.jobtype.JobType;

public record ChatResponse(
        String name,
        String content,
        Timestamp timestamp,
        Boolean isOwner,
        JobType job
) {


    public static ChatResponse of(
            Message message,
            boolean isOwner,
            boolean isMafia,
            JobType jobType
    ) {
        return new ChatResponse(
                message.getName(),
                message.getContent(),
                new Timestamp(message.getTimestamp()),
                isOwner,
                filteringMafia(isOwner, isMafia, jobType)
        );
    }

    private static JobType filteringMafia(
            final boolean isOwner,
            final boolean isMafia,
            final JobType jobType
    ) {
        if (isMafia && jobType.equals(JobType.MAFIA)) {
            return jobType;
        }
        if (isOwner) {
            return jobType;
        }
        return null;
    }
}
