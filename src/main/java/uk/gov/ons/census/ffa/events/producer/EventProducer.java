package uk.gov.ons.census.ffa.events.producer;

import uk.gov.ons.census.ffa.events.data.ErrorEvent;
import uk.gov.ons.census.ffa.events.data.Event;

public interface EventProducer {
  public static final String INVALID_ERROR_TYPE = "Invalid Error Type";
  
  void sendEvent(Event event);

  void sendErrorEvent(ErrorEvent errorEvent);
}
