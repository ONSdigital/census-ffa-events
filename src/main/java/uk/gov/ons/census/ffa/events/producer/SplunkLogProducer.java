package uk.gov.ons.census.ffa.events.producer;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import org.springframework.stereotype.Component;

import uk.gov.ons.census.ffa.events.data.ErrorEvent;
import uk.gov.ons.census.ffa.events.data.Event;

@Component
public class SplunkLogProducer implements EventProducer {
  private static final Logger log = LoggerFactory.getLogger(SplunkLogProducer.class);

  @Override
  public void sendEvent(Event event) {
    log.info(event.getContext() + event.getEventType());
    log.with("event", event).info("{} event", event.getSource());
  }

  @Override
  public void sendErrorEvent(ErrorEvent errorEvent) {
    if (errorEvent.getMetadata() != null && errorEvent.getMetadata()
        .containsKey(EventProducer.INVALID_ERROR_TYPE)) {
      log.error("Invalid event type: {}", errorEvent.getMetadata().get(EventProducer.INVALID_ERROR_TYPE));
    }
    log.with("error event", errorEvent)
        .info("{} {} error event", errorEvent.getSource(), errorEvent.getErrorEventType());
  }
}

