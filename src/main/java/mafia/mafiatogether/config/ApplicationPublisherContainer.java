package mafia.mafiatogether.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public abstract class ApplicationPublisherContainer {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private static ApplicationEventPublisher publisher;

   @PostConstruct
   private void setPublisher(){
       publisher = this.applicationEventPublisher;
   }

    public static ApplicationEventPublisher getPublisher() {
        return publisher;
    }
}
