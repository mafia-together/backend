package mafia.mafiatogether.game.annotation;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SseSubscribe {
}
