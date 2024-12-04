package mafia.mafiatogether.common.interceptor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class PathMatcherInterceptor implements ChannelInterceptor {

    private final ChannelInterceptor channelInterceptor;
    private final PathMatcher pathMatcher;
    private final List<StompMapping> includePathPattern;
    private final List<StompMapping> excludePathPattern;

    public PathMatcherInterceptor(
            final ChannelInterceptor channelInterceptor
    ) {
        this.channelInterceptor = channelInterceptor;
        this.pathMatcher = new AntPathMatcher();
        this.includePathPattern = new ArrayList<>();
        this.excludePathPattern = new ArrayList<>();
    }


    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (
                headerAccessor.getDestination() != null &&
                        notIncludedPath(headerAccessor.getDestination(), headerAccessor.getCommand())
        ) {
            return channelInterceptor.preSend(message, channel);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private boolean notIncludedPath(String destination, StompCommand command) {
        boolean excludePattern = excludePathPattern.stream()
                .anyMatch(stompMapping -> anyMatchPathPattern(destination, command, stompMapping));

        boolean includePattern = includePathPattern.stream()
                .anyMatch(stompMapping -> anyMatchPathPattern(destination, command, stompMapping));

        return excludePattern || !includePattern;
    }

    private boolean anyMatchPathPattern(String destination, StompCommand command, StompMapping stompMapping) {
        return pathMatcher.match(destination, stompMapping.destination()) &&
                stompMapping.command() == command;
    }

    public PathMatcherInterceptor includePathPattern(String targetPath, StompCommand pathMethod) {
        this.includePathPattern.add(new StompMapping(targetPath, pathMethod));
        return this;
    }

    public PathMatcherInterceptor excludePathPattern(String targetPath, StompCommand pathMethod) {
        this.excludePathPattern.add(new StompMapping(targetPath, pathMethod));
        return this;
    }

}
