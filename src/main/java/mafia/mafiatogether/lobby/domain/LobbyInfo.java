package mafia.mafiatogether.lobby.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.GameException;
import mafia.mafiatogether.job.domain.jobtype.Doctor;
import mafia.mafiatogether.job.domain.jobtype.Job;
import mafia.mafiatogether.job.domain.jobtype.Mafia;
import mafia.mafiatogether.job.domain.jobtype.Police;

@Getter
@RequiredArgsConstructor
public class LobbyInfo {

    private final int total;
    private final int mafia;
    private final int doctor;
    private final int police;

    public static LobbyInfo of(final int total, final int mafia, final int doctor, final int police) {
        if (isInvalidJobBalance(total, mafia)) {
            throw new GameException(ExceptionCode.INVALID_ROOM_INFORMATION);
        }
        return new LobbyInfo(total, mafia, doctor, police);
    }

    private static boolean isInvalidJobBalance(int total, int mafia) {
        return total / 2 < mafia || total < 3 || mafia == 0;
    }

    public Queue<Job> getRandomJobQueue() {
        final List<Job> jobList = new ArrayList<>();
        addTimes(new Mafia(), mafia, jobList);
        addTimes(new Doctor(), doctor, jobList);
        addTimes(new Police(), police, jobList);
        Collections.shuffle(jobList);
        return new LinkedList<>(jobList);
    }

    private void addTimes(final Job job, int times, final List<Job> jobList) {
        for (int i = 0; i < times; i++) {
            jobList.add(job);
        }
    }
}
