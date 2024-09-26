package mafia.mafiatogether.chat.application;

import lombok.RequiredArgsConstructor;
import mafia.mafiatogether.chat.application.dto.ChatV2Response;
import mafia.mafiatogether.chat.domain.Chat;
import mafia.mafiatogether.chat.domain.ChatRepository;
import mafia.mafiatogether.chat.domain.Message;
import mafia.mafiatogether.common.exception.ExceptionCode;
import mafia.mafiatogether.common.exception.GameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ChatV2Service {

    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<ChatV2Response> findAllChat(final String code, final String name) {
        final Chat chat = chatRepository.findById(code)
                .orElseThrow(() -> new GameException(ExceptionCode.INVALID_NOT_FOUND_ROOM_CODE));

        return chat.getMessages()
                .stream()
                .map(message -> ChatV2Response.of(
                        message,
                        message.getName().equals(name)
                )).toList();
    }

    @Transactional
    public Message saveChat(final String name, final String code, final String content) {
        Chat chat = chatRepository.findById(code)
                .orElseThrow(NoSuchElementException::new);
        Message message = Message.of(name, content);
        chat.saveMessage(message);
        chatRepository.save(chat);
        return message;
    }


}
