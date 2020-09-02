package uk.gov.ons.census.ffa.events.producer;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import uk.gov.ons.census.ffa.events.data.ErrorEvent;
import uk.gov.ons.census.ffa.events.data.Event;

//@Component
public class RabbitMQEventProducer implements EventProducer {

  private static final Logger log = LoggerFactory.getLogger(RabbitMQEventProducer.class);

  @Autowired
  @Qualifier("GW_EVENT_RT")
  private RabbitTemplate rabbitTemplate;

  @Autowired
  @Qualifier("eventExchange")
  private TopicExchange eventExchange;

  @Retryable
  public void sendEvent(final Event event) {
    final String msg = "{Could not parse event.}";
    try {
      rabbitTemplate.convertAndSend(eventExchange.getName(), event.getContext(), event);
    } catch (final Exception e) {
      log.error("Failed to log RabbitMQ Event: {}", msg, e);
    }
  }

  @Override
  public void sendErrorEvent(final ErrorEvent errorEvent) {
    final String msg = "{Could not parse event.}";
    try {
      rabbitTemplate.convertAndSend(eventExchange.getName(), errorEvent.getContext(), errorEvent);
    } catch (final Exception e) {
      log.error("Failed to log RabbitMQ Event: {}", msg, e);
    }
  }
}
