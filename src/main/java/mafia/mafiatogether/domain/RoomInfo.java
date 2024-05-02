package mafia.mafiatogether.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.domain.role.Doctor;
import mafia.mafiatogether.domain.role.Mafia;
import mafia.mafiatogether.domain.role.Police;
import mafia.mafiatogether.domain.role.Job;

@Getter
@RequiredArgsConstructor
public class RoomInfo {

    private final int total;
    private final int mafia;
    private final int doctor;
    private final int police;

    public Queue<Job> getRandomJobQueue() {
        List<Job> jobList = new ArrayList<>();
        addTimes(new Mafia(), mafia, jobList);
        addTimes(new Doctor(), doctor, jobList);
        addTimes(new Police(), police, jobList);
        Collections.shuffle(jobList);
        return new LinkedList<>(jobList);
    }

    private void addTimes(final Job job, int times, final List<Job> jobList){
        for (int i = 0; i < times; i++) {
            jobList.add(job);
        }
    }
}
