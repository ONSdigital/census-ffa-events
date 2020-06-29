package uk.gov.ons.census.fwmt.events.producer;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

@Component
public class SplunkLogGatewayEventProducer implements GatewayEventProducer {
  private static final Logger log = LoggerFactory.getLogger(SplunkLogGatewayEventProducer.class);

  @Override
  public void sendEvent(GatewayEventDTO event) {
    log.info(event.getContext() + event.getEventType());
    log.with("event", event).info("{} event", event.getSource());
  }

  @Override
  public void sendErrorEvent(GatewayErrorEventDTO errorEvent) {
    if (errorEvent.getMetadata() != null && errorEvent.getMetadata()
        .containsKey(GatewayEventProducer.INVALID_ERROR_TYPE)) {
      log.error("Invalid event type: {}", errorEvent.getMetadata().get(GatewayEventProducer.INVALID_ERROR_TYPE));
    }
    log.with("error event", errorEvent)
        .info("{} {} error event", errorEvent.getSource(), errorEvent.getErrorEventType());
  }
}

