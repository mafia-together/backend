package mafia.mafiatogether.chat.application;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.application.dto.request.ChatRequest;
import mafia.mafiatogether.chat.application.dto.response.ChatResponse;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.config.exception.ExceptionCode;
import mafia.mafiatogether.config.exception.RoomException;
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final PlayerJobRepository playerJobRepository;
    private final ChatRepository chatRepository;

    public List<ChatResponse> findAllChat(final String code, final String name) {
        final Map<String, JobType> playerJobs = convertPlayerJobsToMap(playerJobRepository.findByCode(code));
        final Chat chat = chatRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final boolean isMafia = playerJobs.get(name).equals(JobType.MAFIA);
        return chat.getMessages().stream()
                .map(message -> ChatResponse.of(message, name, isMafia, playerJobs.get(message.getName())))
                .toList();
    }

    private static Map<String, JobType> convertPlayerJobsToMap(final List<PlayerJob> playerJobs) {
        final Map<String, JobType> playerJobMap = new HashMap<>();
        for (PlayerJob playerJob : playerJobs) {
            playerJobMap.put(playerJob.getName(), playerJob.getJob().getJobType());
        }
        return playerJobMap;
    }

    public void saveChat(final String code, final String name, final ChatRequest chatRequest) {
        final Chat chat = chatRepository.findById(code)
                .orElseThrow(() -> new RoomException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));
        final Message message = new Message(name, chatRequest.contents(), Clock.systemDefaultZone().millis());
        chat.saveMessage(message);
        chatRepository.save(chat);
    }
}
