package mafia.mafiatogether.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode {

    INVALID_REQUEST(100, "잘못된 요청입니다."),
    INVALID_ROOM_INFORMATION(101, "방 구성이 잘 못 되었습니다."),
    INVALID_NOT_FOUND_ROOM_CODE(102, "해당 코드의 방이 존재하지 않습니다"),
    INVALID_NAMES(103, "이미 존재하는 이름입니다."),
    ROOM_FULL(104, "이미 방이 꽉 찼습니다."),
    NOT_ENOUGH_PLAYER(105, "인원이 부족합니다."),
    INVALID_CONTENT(106, "내용이 누락 되었습니다."),
    NOT_ALIVE_PLAYER(107, "이미 사망한 사람입니다."),
    INVALID_PLAYER(108, "존재 하지 않는 사람입니다."),

    NOT_PARTICIPATING_GAME(200, "게임에 참가해 주십시오."),
    INVALID_JOB(201, "마피아만 지목 대상을 볼 수 있습니다."),

    NOT_FOUND_REQUEST(300,"HttpServletRequest를 찾을 수 없습니다."),
    MISSING_AUTHENTICATION_HEADER(301,"인증 헤더가 없습니다."),
    INVALID_AUTHENTICATION_FORM(302,"올바른 인증 형식이 아닙니다.");

    private final int code;
    private final String message;
}