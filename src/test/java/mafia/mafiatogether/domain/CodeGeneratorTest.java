package mafia.mafiatogether.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class CodeGeneratorTest {

    @Test
    void 랜덤_코드를_생성할_수_있다() {
        String code = CodeGenerator.generate();

        Assertions.assertThat(code).hasSize(10);
    }
}
