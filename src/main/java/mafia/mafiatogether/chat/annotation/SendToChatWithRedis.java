package mafia.mafiatogether.chat.annotation;

import java.lang.annotation.*;

/**
 * 해당 어노테이션이 붙어있는 Controller가 반환하는 값을 StringRedisTemplate을 통해 Send 해줍니다.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SendToChatWithRedis {

   /**
     * 명시되어 있는 값을 활용하여 Topic을 정하고 Redis에 Publish 합니다.
     * <p>
     * 또한, 텍스트 대치가 가능하도록 했습니다.
     * <p>
     * ex) /sub/chat/{code} 라고 했을 때, Parameter에 code가 있으면 {code}를 code 값으로 대치합니다.
     * <pre>
     * &#64;SendToChatWithRedis("/sub/chat/{code}")
     * public Message createChat(String code) {
     * }
     * </pre>
     * <p>
     * 위와 같이 명시되어 있고, code == "mafia" 라면, /sub/chat/mafia 로 대치됩니다.
     */
    String value();

}
