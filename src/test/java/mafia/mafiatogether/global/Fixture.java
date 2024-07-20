package mafia.mafiatogether.global;

import java.time.Clock;

public abstract class Fixture {

    public static final Long DAY_INTRO_TIME = Clock.systemDefaultZone().millis();
    public static final Long DAY_INTRO_END_TIME = DAY_INTRO_TIME + 2_000L;
    public static final Long NOTICE_TIME = DAY_INTRO_END_TIME + 1_000L;
    public static final Long NOTICE_END_TIME = NOTICE_TIME + 2_000L;
    public static final Long DAY_TIME = NOTICE_END_TIME + 1_000L;
    public static final Long DAY_END_TIME = DAY_TIME + 59_000L;
    public static final Long VOTE_TIME = DAY_END_TIME + 1_000L;
    public static final Long VOTE_END_TIME = VOTE_TIME + 9_000L;
    public static final Long VOTE_RESULT_TIME = VOTE_END_TIME + 1_000L;
    public static final Long VOTE_RESULT_END_TIME = VOTE_RESULT_TIME + 2_000L;
    public static final Long NIGHT_INTRO_TIME = VOTE_RESULT_END_TIME + 1_000L;
    public static final Long NIGHT_INTRO_END_TIME = NIGHT_INTRO_TIME + 2_000L;
    public static final Long NIGHT_TIME = NIGHT_INTRO_END_TIME + 1_000L;
    public static final Long NIGHT_END_TIME = NIGHT_TIME + 39_000L;
    public static final Long NEXT_DAY = NIGHT_END_TIME + 1_000L;
}
