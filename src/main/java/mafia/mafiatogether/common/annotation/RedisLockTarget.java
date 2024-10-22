package mafia.mafiatogether.common.annotation;

import java.lang.annotation.*;

/**
 * 이 어노테이션은 {@link mafia.mafiatogether.common.annotation.RedisLock}을 사용한 메서드의 String파라미터에 사용됩니다
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
