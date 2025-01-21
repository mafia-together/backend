package mafia.mafiatogether.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 이 어노테이션은 {@link RedisLock}을 사용한 메서드의 String파라미터에 사용됩니다.
 * <p>
 * 작성자: waterricecake
 * <p>
 * 수정일시 : 20241021
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLockTarget {
}
