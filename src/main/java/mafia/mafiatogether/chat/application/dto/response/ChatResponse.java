package mafia.mafiatogether.chat.application.dto.response;

import java.sql.Timestamp;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.job.domain.jobtype.JobType;

public record ChatResponse(
        String name,
        String contents,
        Timestamp timestamp,
        Boolean isOwner,
        JobType job
) {


    public static ChatResponse of(
            Chat chat,
            String name,
            boolean isMafia,
            JobType jobType
    ) {
        final boolean isOwner = chat.getName().equals(name);
        return new ChatResponse(
                chat.getName(),
                chat.getContents(),
                new Timestamp(chat.getTimestamp()),
                isOwner,
                filteringMafia(isOwner, isMafia, jobType)
        );
    }

    private static JobType filteringMafia(
            final boolean isOwner,
            final boolean isMafia,
            final JobType jobType) {
        if (isMafia) {
            return jobType.equals(JobType.MAFIA) ? jobType : null;
        }
        return isOwner ? jobType : null;
    }
}
