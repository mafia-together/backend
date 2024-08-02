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
import mafia.mafiatogether.job.domain.PlayerJob;
import mafia.mafiatogether.job.domain.PlayerJobRepository;
import mafia.mafiatogether.job.domain.jobtype.JobType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final PlayerJobRepository playerJobRepository;
    private final ChatRepository chatRepository;

    public List<ChatResponse> findAllChat(final String code, final String name) {
        final Map<String, JobType> playerJobs = convertPlayerJobsToMap(playerJobRepository.findByCode(code));
        final List<Chat> chats = chatRepository.findByCode(code);
        final boolean isMafia = playerJobs.get(name).equals(JobType.MAFIA);
        return chats.stream()
                .map(chat -> ChatResponse.of(chat, name, isMafia, playerJobs.get(name)))
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
        final Chat chat = new Chat(code, name, chatRequest.contents(), Clock.systemDefaultZone().millis());
        chatRepository.save(chat);
    }
}
