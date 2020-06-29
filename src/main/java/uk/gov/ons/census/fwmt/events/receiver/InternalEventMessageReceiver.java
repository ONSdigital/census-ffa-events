package uk.gov.ons.census.fwmt.events.receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.producer.SplunkLogGatewayEventProducer;

@Component
public class InternalEventMessageReceiver {
  @Autowired
  private SplunkLogGatewayEventProducer splunkLogGatewayEventProducer;

    public void receiveErrorEvent(GatewayErrorEventDTO gatewayErrorEventDTO) {
      splunkLogGatewayEventProducer.sendErrorEvent(gatewayErrorEventDTO);
    }
  
    public void receiveEvent(GatewayEventDTO gatewayEventDTO) {
      splunkLogGatewayEventProducer.sendEvent(gatewayEventDTO);
    }
}