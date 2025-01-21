package mafia.mafiatogether.global;

import java.time.Clock;

public abstract class Fixture {

    public static final Long dayIntroTime = Clock.systemDefaultZone().millis();
    public static final Long dayIntroEndTime = dayIntroTime + 2_000L;
    public static final Long noticeTime = dayIntroEndTime + 1_000L;
    public static final Long noticeEndTime = noticeTime + 2_000L;
    public static final Long dayTime = noticeEndTime + 1_000L;
    public static final Long dayEndTime = dayTime + 59_000L;
    public static final Long voteTime = dayEndTime + 1_000L;
    public static final Long voteEndTime = voteTime + 9_000L;
    public static final Long voteResultTime = voteEndTime + 1_000L;
    public static final Long voteResultEndTime = voteResultTime + 2_000L;
    public static final Long nightIntroTime = voteResultEndTime + 1_000L;
    public static final Long nightIntroEndTime = nightIntroTime + 2_000L;
    public static final Long nightTime = nightIntroEndTime + 1_000L;
    public static final Long nightEndTime = nightTime + 39_000L;
    public static final Long nextDay = nightEndTime + 1_000L;
}
