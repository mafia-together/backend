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
import java.util.function.Supplier;

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
    public Message enter(final String name, final String code) {
        return saveChat(code, () -> Message.fromEnter(name));
    }

    @Transactional
    public Message leave(final String name, final String code) {
        return saveChat(code, () -> Message.fromLeave(name));
    }

    @Transactional
    public Message chat(final String name, final String code, final String content) {
        return saveChat(code, () -> Message.ofChat(name, content));
    }

    private Message saveChat(final String code, Supplier<Message> messageFunction) {
        Chat chat = chatRepository.findById(code)
                .orElseThrow(NoSuchElementException::new);
        Message message = messageFunction.get();
        chat.saveMessage(message);
        chatRepository.save(chat);
        return message;
    }


}
