package uk.gov.ons.census.fwmt.events.producer;

import java.util.Map;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;

import uk.gov.ons.census.fwmt.events.config.GatewayEventQueueConfig;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

@Component
class RabbitMQGatewayEventProducer implements GatewayEventProducer {

  private static final Logger log = LoggerFactory.getLogger(RabbitMQGatewayEventProducer.class);

  @Autowired
  @Qualifier("GW_EVENT_RT")
  private RabbitTemplate rabbitTemplate;

  @Autowired
  @Qualifier("eventExchange")
  private FanoutExchange eventExchange;

  @Autowired
  @Qualifier("eventTopicExchange")
  private TopicExchange eventTopicExchange;

  @Retryable
  public void sendEvent(GatewayEventDTO event) {
    String msg = "{Could not parse event.}";
    try {
      String rk = getRoutingKey(event.getMetadata(), GatewayEventQueueConfig.GATEWAY_EVENTS_ROUTING_KEY);
      rabbitTemplate.convertAndSend(getExchangeName(event.getMetadata()), rk, event);
    } catch (Exception e) {
      log.error("Failed to log RabbitMQ Event: {}", msg, e);
    }
  }

  @Override
  public void sendErrorEvent(GatewayErrorEventDTO errorEvent) {
    String msg = "{Could not parse event.}";
    try {
        String rk = getRoutingKey(errorEvent.getMetadata(), GatewayEventQueueConfig.GATEWAY_EVENTS_ROUTING_KEY);
      rabbitTemplate.convertAndSend(eventExchange.getName(), rk, errorEvent);
    } catch (Exception e) {
      log.error("Failed to log RabbitMQ Event: {}", msg, e);
    }
  }

  private String getRoutingKey(Map<String, String> metadata, String defaultRK) {
	if (metadata!=null) {
		if (metadata.keySet().contains("topic")) {
			String topic = metadata.get("topic");
			if (topic!=null) return topic;
		}
	}
	return defaultRK;
  }

  private String getExchangeName(Map<String, String> metadata) {
	if (metadata!=null) {
		if (metadata.keySet().contains("topic")) {
			return eventTopicExchange.getName();
		}
	}
	return eventExchange.getName();
  }
}
