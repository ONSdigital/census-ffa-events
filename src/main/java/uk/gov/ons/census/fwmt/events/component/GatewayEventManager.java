package uk.gov.ons.census.fwmt.events.component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO.GatewayErrorEventDTOBuilder;
import uk.gov.ons.census.fwmt.events.producer.GatewayEventProducer;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;

@Slf4j
public class GatewayEventManager {

  @Autowired
  private List<GatewayEventProducer> gatewayEventProducers;

  private List<String> eventTypes = new ArrayList<>();

  private List<String> errorEventTypes = new ArrayList<>();

  private String source;
  
  public void addEventTypes(String[] et) {
    for (String e : et) {
      eventTypes.add(e);
    }
  }

  public void addErrorEventTypes(String[] et) {
    for (String e : et) {
      errorEventTypes.add(e);
    }
  }

  public void setSource(String source) {
    this.source = source;
  }
  
  public void triggerEvent(String caseId, String eventType){
    triggerEvent(caseId, eventType, new HashMap<>());
  }

  public void triggerEvent(String caseId, String eventType, Map<String, String> metadata) {
    if (eventTypes.contains(eventType)) {
      GatewayEventDTO gatewayEventDTO = GatewayEventDTO.builder()
          .caseId(caseId).source(source).eventType(eventType).localTime(LocalTime.now()).metadata(metadata)
          .build();
      for (GatewayEventProducer gep : gatewayEventProducers) {
        gep.sendEvent(gatewayEventDTO);
      }
    } else {
      log.error("Invalid event type: {}", eventType);
    }
  }

  public void triggerErrorEvent(Class klass, Exception exception, String message, String caseId, String errorEventType) {
    triggerErrorEvent(klass, exception, message, caseId, errorEventType, new HashMap<>());
  }

  public void triggerErrorEvent(Class klass, String message, String caseId, String errorEventType) {
    triggerErrorEvent(klass, null, message, caseId, errorEventType, new HashMap<>());
  }

  public void triggerErrorEvent(Class klass, Exception exception, String message, String caseId, String errorEventType, Map<String, String> metadata) {
    GatewayErrorEventDTOBuilder builder = GatewayErrorEventDTO.builder()
        .className(klass.getName()).exceptionName((exception != null) ? exception.getClass().getName() : "<NONE>").message(message)
        .caseId(caseId).errorEventType(errorEventType).source(source).localTime(LocalTime.now()).metadata(metadata);

    if (errorEventTypes.contains(errorEventType)) {
      builder.errorEventType(errorEventType);
    } else {
      if (metadata==null) metadata = new HashMap<String, String>();
      metadata.put(GatewayEventProducer.INVALID_ERROR_TYPE, errorEventType);
    }
    for (GatewayEventProducer gep : gatewayEventProducers) {
      gep.sendErrorEvent(builder.build());
    }
  }

}
