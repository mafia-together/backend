package mafia.mafiatogether.config;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;

import mafia.mafiatogether.common.AuthExtractor;
import mafia.mafiatogether.config.exception.AuthException;
import mafia.mafiatogether.config.exception.ExceptionCode;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PlayerArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PlayerInfo.class);
    }

    @Override
    public PlayerInfoDto resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                         final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        if (httpServletRequest == null) {
            throw new AuthException(ExceptionCode.NOT_FOUND_REQUEST);
        }

        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization == null) {
            throw new AuthException(ExceptionCode.MISSING_AUTHENTICATION_HEADER);
        }

        String[] information = AuthExtractor.extractBy(authorization);
        return new PlayerInfoDto(information[0], information[1]);
    }
}
