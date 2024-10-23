package mafia.mafiatogether.common.annotation;

import java.lang.annotation.*;

/**
 * 이 어노테이션은 서비스단에서 락대상을 정하는 메서드입니다.
 * 해당 메서드에서 조회하는 모든 키에 대해 Lock을 거는 것이 아닌 특정 key에만 설정해줍니다.
 * 또한 이 Lock이 어노테이션이 없는 대상은 해당 자원에 접근할 수 있습니다.
 * 이 어노테이션은 이 어노테이션을 사용하는 메서드끼리만의 자원 접근을 막을 수 있습니다.
 * <p>
 * 작성자: waterricecake
 * </p>
 * 수정일시 : 20241021
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    /**
     * key()는 자원의 유니크한 키를 사용하기 위해 사용합니다.
     * <pre>
     *  ex) @RedisLock(key = "lobby") 를 사용할 경우 키는 "mafiatogether:lock:lobby"가 완성됩니다. 이후 뒤는 랜덤한 값(code)를 사용하여 유니크하게 유지합니다.
     * </pre>
     * @exception mafia.mafiatogether.common.exception.ServerException if {@link mafia.mafiatogether.common.annotation.RedisLockTarget} not exists
     * <p>
     * 작성자: waterricecake
     * </p>
     * <p>
     * 수정일시 : 20241021
     * </p>
     */
    String[] key();
}
