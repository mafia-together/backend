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
    private final List<StompMapping> includePathPatterns;
    private final List<StompMapping> excludePathPatterns;

    public PathMatcherInterceptor(final ChannelInterceptor channelInterceptor) {
        this.channelInterceptor = channelInterceptor;
        this.pathMatcher = new AntPathMatcher();
        this.includePathPatterns = new ArrayList<>();
        this.excludePathPatterns = new ArrayList<>();
    }

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (headerAccessor != null &&
                headerAccessor.getDestination() != null &&
                shouldIntercept(headerAccessor.getDestination(), headerAccessor.getCommand())) {
            return channelInterceptor.preSend(message, channel);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private boolean shouldIntercept(String destination, StompCommand command) {
        boolean isExcluded = excludePathPatterns.stream()
                .anyMatch(stompMapping -> matchesPathAndCommand(destination, command, stompMapping));

        boolean isIncluded = includePathPatterns.stream()
                .anyMatch(stompMapping -> matchesPathAndCommand(destination, command, stompMapping));

        System.out.println("##");
        System.out.println("##");
        System.out.println(isExcluded);
        System.out.println(isIncluded);

        return isIncluded && !isExcluded;
    }

    private boolean matchesPathAndCommand(String destination, StompCommand command, StompMapping stompMapping) {
        return pathMatcher.match(stompMapping.destination(), destination) &&
                stompMapping.command() == command;
    }

    public PathMatcherInterceptor includePathPattern(String targetPath, StompCommand command) {
        this.includePathPatterns.add(new StompMapping(targetPath, command));
        return this;
    }

    public PathMatcherInterceptor excludePathPattern(String targetPath, StompCommand command) {
        this.excludePathPatterns.add(new StompMapping(targetPath, command));
        return this;
    }

}
