package mafia.mafiatogether.common.aspect;

import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.annotation.SseSubscribe;
import mafia.mafiatogether.common.resolver.PlayerInfoDto;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.mockito.Mockito.mock;

@TestComponent
public class SseAspectTestService {

    @SseSubscribe
    public SseEmitter subscribe(@PlayerInfo final PlayerInfoDto playerInfoDto){
        return mock(SseEmitter.class);
    }
}
