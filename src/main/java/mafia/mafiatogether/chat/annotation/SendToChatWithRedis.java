package mafia.mafiatogether.chat.annotation;

import java.lang.annotation.*;

/**
 * 해당 어노테이션이 붙어있는 Controller가 반환하는 값을 StringRedisTemplate을 통해 Send 해줍니다.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SendToChatWithRedis {

    /**
     * 명시되어 있는 값을 활용하여 Topic을 정하고 Redis에 Publish 합니다.
     */
    String value();

}
