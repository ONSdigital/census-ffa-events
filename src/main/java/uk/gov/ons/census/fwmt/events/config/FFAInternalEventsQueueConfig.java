package uk.gov.ons.census.fwmt.events.config;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.ons.census.fwmt.events.component.EventTrigger;
import uk.gov.ons.census.fwmt.events.receiver.InternalEventMessageReceiver;

@Configuration
public class FFAInternalEventsQueueConfig {

    @Bean
    public Queue internalEventsQueue() {
        return new AnonymousQueue();
    }
    
    @Bean
    public Binding internalEventsBinding(@Qualifier("eventExchange") TopicExchange topicExchange, @Qualifier("internalEventsQueue") Queue internalEventsQueue, EventTrigger eventTrigger) {
        return BindingBuilder.bind(internalEventsQueue).to(topicExchange).with(eventTrigger.getContextPrefix()+".*");
    }

    @Bean
    public MessageListenerAdapter internalEventsListenerAdapter(InternalEventMessageReceiver receiver) {
      return new MessageListenerAdapter(receiver, "receiveEvent");
    }
    
    @Bean
    public SimpleMessageListenerContainer internalEventsListener(
        ConnectionFactory connectionFactory,
        @Qualifier("internalEventsListenerAdapter") MessageListenerAdapter messageListenerAdapter, @Qualifier("internalEventsQueue") Queue internalEventsQueue) {
      SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
  //      Advice[] adviceChain = {retryOperationsInterceptor};
      messageListenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
      container.setPrefetchCount(1);
      //    container.setAdviceChain(adviceChain);
      container.setConnectionFactory(connectionFactory);
      container.setQueueNames(internalEventsQueue.getName());
      container.setMessageListener(messageListenerAdapter);
      container.setAutoStartup(true);
      container.setConcurrentConsumers(1);
      return container;
    }
}