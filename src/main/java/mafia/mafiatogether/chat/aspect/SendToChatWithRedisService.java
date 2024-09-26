package mafia.mafiatogether.chat.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.annotation.SendToChatWithRedis;
import mafia.mafiatogether.chat.domain.Message;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
public class SendToChatWithRedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(mafia.mafiatogether.chat.annotation.SendToChatWithRedis)")
    public Object sendToChat(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        Map<String, String> parameterToValue = mapParameterAndValue(method.getParameters(), proceedingJoinPoint.getArgs());
        SendToChatWithRedis sendToChatWithRedisAnnotation = method.getAnnotation(SendToChatWithRedis.class);
        String topic = replacePlaceholders(sendToChatWithRedisAnnotation.value(), parameterToValue);

        Message message = (Message) proceedingJoinPoint.proceed();
        stringRedisTemplate.convertAndSend(topic, objectMapper.writeValueAsString(message));

        return message;
    }

    private Map<String, String> mapParameterAndValue(Parameter[] parameters, Object[] args) {
        Map<String, String> parameterToArg = new LinkedHashMap<>();

        for (int i = 0; i < parameters.length; i++) {
            try {
                parameterToArg.put(parameters[i].getName(), (String) args[i]);
            } catch (Exception ignored) {
            }
        }

        return parameterToArg;
    }

    private static String replacePlaceholders(String template, Map<String, String> parameterToValue) {
        for (Map.Entry<String, String> entry : parameterToValue.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }

}
