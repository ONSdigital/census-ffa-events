package uk.gov.ons.census.ffa.events.receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.ons.census.ffa.events.data.ErrorEvent;
import uk.gov.ons.census.ffa.events.data.Event;
import uk.gov.ons.census.ffa.events.producer.SplunkLogProducer;

//@Component
public class InternalEventMessageReceiver {
  @Autowired
  private SplunkLogProducer splunkLogProducer;

    public void receiveErrorEvent(ErrorEvent errorEventDTO) {
      splunkLogProducer.sendErrorEvent(errorEventDTO);
    }
  
    public void receiveEvent(Event eventDTO) {
      splunkLogProducer.sendEvent(eventDTO);
    }
}