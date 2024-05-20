package mafia.mafiatogether.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class VoteTest {

    @Test
    void 투표를_할_수_있다() {
        // given
        final Vote vote = Vote.create();
        final Player p1 = Player.create("p1");
        final Player p2 = Player.create("p2");
        final Player p3 = Player.create("p3");
        final Player p4 = Player.create("p4");
        final Player p5 = Player.create("p5");

        vote.choose(p1, p4);
        vote.choose(p2, p4);
        vote.choose(p3, p4);
        vote.choose(p4, p1);
        vote.choose(p5, p1);

        // when
        vote.executeVote();

        // then
        Assertions.assertThat(vote.getVoteResult()).isEqualTo(p4.getName());
    }

    @Test
    void 동점표_일_경우_무효표가_된다() {
        // given
        final Vote vote = Vote.create();
        final Player p1 = Player.create("p1");
        final Player p2 = Player.create("p2");
        final Player p3 = Player.create("p3");
        final Player p4 = Player.create("p4");
        final Player p5 = Player.create("p5");

        vote.choose(p1, p4);
        vote.choose(p2, p4);
        vote.choose(p4, p1);
        vote.choose(p5, p1);

        // when
        vote.executeVote();

        // then
        Assertions.assertThat(vote.getVoteResult()).isBlank();
    }
}
