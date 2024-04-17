package mafia.mafiatogether.service.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ChatResponse {

    private final String name;
    private final String contents;
    private final Timestamp timestamp;
    private final boolean owner;
}
