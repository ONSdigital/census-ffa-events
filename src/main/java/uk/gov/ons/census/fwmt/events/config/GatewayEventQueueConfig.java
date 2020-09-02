package uk.gov.ons.census.fwmt.events.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

@Configuration
public class GatewayEventQueueConfig {

  public static final String GATEWAY_EVENTS_ROUTING_KEY = "Gateway.Event";
  public static final String GATEWAY_EVENTS_EXCHANGE = "Gateway.Events.Exchange";
  public static final String FFA_EVENTS_EXCHANGE = "FFA.Events.Exchange";

  @Bean
  public FanoutExchange eventExchange() {
    return new FanoutExchange(GATEWAY_EVENTS_EXCHANGE);
  }  
  
  @Bean
  public TopicExchange eventTopicExchange() {
    return new TopicExchange(FFA_EVENTS_EXCHANGE);
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
    idClassMapping.put("uk.gov.ons.census.fwmt.events.data.GatewayEventDTO", GatewayEventDTO.class);
    idClassMapping.put("uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO", GatewayErrorEventDTO.class);
    classMapper.setIdClassMapping(idClassMapping);
    return classMapper;
  }
}
