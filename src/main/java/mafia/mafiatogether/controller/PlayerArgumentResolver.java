package mafia.mafiatogether.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import mafia.mafiatogether.service.dto.PlayerInfoDto;
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
            throw new IllegalArgumentException("HttpServletRequest를 찾을 수 없습니다.");
        }

        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization == null) {
            throw new IllegalArgumentException("인증 헤더가 없습니다.");
        }

        String[] token = authorization.split(" ");
        if (token.length != 2 || !token[0].equals("Basic")) {
            throw new IllegalArgumentException("올바른 인증 형식이 아닙니다.");
        }

        String[] info = new String(Base64.getDecoder().decode(token[1])).split(":");
        if (info.length != 2) {
            throw new IllegalArgumentException("올바른 인증 형식이 아닙니다.");
        }

        return new PlayerInfoDto(info[0], info[1]);
    }
}
