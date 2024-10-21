package mafia.mafiatogether.common.aspect;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.common.annotation.RedisLockTarget;
import mafia.mafiatogether.common.annotation.RedisLock;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.ServerException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisTransactionAspect {

    private static final String LOCK_KEY_PREFIX = "mafiatogether:lock:";
    public static final int WAIT_TIME = 5;
    public static final int LEASE_TIME = 15;
    private final RedissonClient redissonClient;

    @Around("@annotation(mafia.mafiatogether.common.annotation.RedisLock)")
    public Object lock(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String[] keys = getRedisLockKey(proceedingJoinPoint);
        final RLock[] locks = getRLocks(keys);
        final RedissonMultiLock multiLock = new RedissonMultiLock(locks);

        boolean isLocked;

        try {
            isLocked = multiLock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ServerException(ExceptionCode.GETTING_LOCK_FAIL_EXCEPTION);
        }

        if (!isLocked) {
            throw new ServerException(ExceptionCode.GETTING_LOCK_FAIL_EXCEPTION);
        }

        try {
            return proceedingJoinPoint.proceed();
        } finally {
            multiLock.unlock();
        }
    }

    private String[] getRedisLockKey(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object[] args = proceedingJoinPoint.getArgs();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (hasTarget(parameterAnnotations[i])) {
                return parsingToKeyList(redisLock.key(), args[i].toString());
            }
        }
        throw new ServerException(ExceptionCode.LOCK_CODE_EXCEPTION);
    }

    private String[] parsingToKeyList(final String[] keys, final String target) {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = LOCK_KEY_PREFIX + keys[i] + ":" + target;
        }
        return keys;
    }

    private RLock[] getRLocks(final String[] keys) {
        RLock[] rLocks = new RLock[keys.length];
        for (int i = 0; i < keys.length; i++) {
            rLocks[i] = redissonClient.getLock(keys[i]);
        }
        return rLocks;
    }

    private boolean hasTarget(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(RedisLockTarget.class::isInstance);
    }
}
