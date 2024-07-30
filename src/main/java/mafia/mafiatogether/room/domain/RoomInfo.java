package mafia.mafiatogether.room.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.global.config.exception.ExceptionCode;
import mafia.mafiatogether.global.config.exception.RoomException;
import mafia.mafiatogether.job.domain.Doctor;
import mafia.mafiatogether.job.domain.Job;
import mafia.mafiatogether.job.domain.Mafia;
import mafia.mafiatogether.job.domain.Police;

@Getter
@RequiredArgsConstructor
public class RoomInfo {

    private final int total;
    private final int mafia;
    private final int doctor;
    private final int police;

    public static RoomInfo of(final int total, final int mafia, final int doctor, final int police) {
        if (total / 2 < mafia || total < 3 || mafia == 0) {
            throw new RoomException(ExceptionCode.INVALID_ROOM_INFORMATION);
        }
        return new RoomInfo(total, mafia, doctor, police);
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
