package uk.gov.ons.census.fwmt.events.producer;

import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

public interface GatewayEventProducer {
  public static final String INVALID_ERROR_TYPE = "Invalid Error Type";
  
  void sendEvent(GatewayEventDTO event);

  void sendErrorEvent(GatewayErrorEventDTO errorEvent);
}
