package mafia.mafiatogether.job.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mafia.mafiatogether.config.exception.PlayerException;
import mafia.mafiatogether.game.domain.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class PoliceTest {

    @Test
    void 이미_스킬을_사용한_경우_예외가_발생한다() {
        //given
        Police police = new Police();
        Mafia mafia = new Mafia();

        Player player = Player.create("player");
        player.modifyJob(mafia);

        JobTarget jobTarget = new JobTarget();
        police.applySkill(player, jobTarget);
        assertThatThrownBy(() -> police.applySkill(player, jobTarget))
                .isInstanceOf(PlayerException.class)
                .hasMessage("이미 스킬을 사용했습니다.");
    }

    @Test
    void 경찰은_마피아를_확인할_수_있다() {
        //given
        Police police = new Police();
        Mafia mafia = new Mafia();

        Player player = Player.create("player");
        player.modifyJob(mafia);

        //then
        Assertions.assertThat(police.applySkill(player, new JobTarget())).isEqualTo("MAFIA");
    }

    @Test
    void 경찰은_시민을_확인할_수_있다() {
        //given
        Police police = new Police();
        Doctor mafia = new Doctor();

        Player player = Player.create("player");
        player.modifyJob(mafia);

        //then
        Assertions.assertThat(police.applySkill(player, new JobTarget())).isEqualTo("CITIZEN");
    }
}
