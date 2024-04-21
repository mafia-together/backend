package mafia.mafiatogether.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class RequestDecrypter {

    public String decryptCode(final HttpServletRequest httpServletRequest) {
        final String[] info = decryptRequest(httpServletRequest);
        return info[0];
    }

    public String decryptName(final HttpServletRequest httpServletRequest) {
        final String[] info = decryptRequest(httpServletRequest);
        return info[1];
    }

    private String[] decryptRequest(final HttpServletRequest httpServletRequest) {
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

        return info;
    }
}
