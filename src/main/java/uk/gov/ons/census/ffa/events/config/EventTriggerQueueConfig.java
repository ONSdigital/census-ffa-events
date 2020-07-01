package uk.gov.ons.census.ffa.events.config;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.ons.census.ffa.events.data.ErrorEvent;
import uk.gov.ons.census.ffa.events.data.Event;

@Configuration
public class EventTriggerQueueConfig {
  //TODO These need to be FFA.Event.* or can be configured by the clients
  public static final String EVENTS_TRIGGER_EXCHANGE = "Events.Trigger.Exchange";

  @Bean
  public TopicExchange eventExchange() {
    return new TopicExchange(EVENTS_TRIGGER_EXCHANGE);
  }

  @Bean("GW_EVENT_MC")
  public MessageConverter jsonMessageConverter(@Qualifier("GW_EVENT_CM") DefaultClassMapper classMapper) {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
    jsonMessageConverter.setClassMapper(classMapper);
    return jsonMessageConverter;
  }

  @Bean("GW_EVENT_RT")
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, @Qualifier("GW_EVENT_MC") MessageConverter messageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    return template;
  }

  @Bean("GW_EVENT_CM")
  public DefaultClassMapper classMapper() {
    DefaultClassMapper classMapper = new DefaultClassMapper();
    Map<String, Class<?>> idClassMapping = new HashMap<>();
    idClassMapping.put("uk.gov.ons.census.fwmt.events.data.GatewayEventDTO", Event.class);
    idClassMapping.put("uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO", ErrorEvent.class);
    classMapper.setIdClassMapping(idClassMapping);
    return classMapper;
  }
}
