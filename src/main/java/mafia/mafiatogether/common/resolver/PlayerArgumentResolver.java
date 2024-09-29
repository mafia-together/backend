package mafia.mafiatogether.common.resolver;

import jakarta.servlet.http.HttpServletRequest;

import mafia.mafiatogether.common.util.AuthExtractor;
import mafia.mafiatogether.common.annotation.PlayerInfo;
import mafia.mafiatogether.common.exception.AuthException;
import mafia.mafiatogether.common.exception.ExceptionCode;
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

        String[] information = AuthExtractor.extractByAuthorization(authorization);
        return new PlayerInfoDto(information[0], information[1]);
    }
}
