package uk.gov.ons.census.fwmt.events.component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO;
import uk.gov.ons.census.fwmt.events.data.GatewayErrorEventDTO.GatewayErrorEventDTOBuilder;
import uk.gov.ons.census.fwmt.events.data.GatewayEventDTO;
import uk.gov.ons.census.fwmt.events.producer.RabbitMQGatewayEventProducer;

public class EventTrigger {

  @Autowired
  private RabbitMQGatewayEventProducer internalEventProducer;

  @Setter
  private String source;

  @Getter
  @Setter
  private String contextPrefix;

  public void debug(GatewayEventDTO event) {
    String eventContext = buildEventContext(contextPrefix, event.getContext());
    event.setContext(eventContext);
    // Map<String, String> metaDataMap = createMetaDataMap(metadata);
    //       GatewayEventDTO gatewayEventDTO = GatewayEventDTO.builder()
    //           .caseId(caseId).source(source).eventType(eventType).localTime(new Date()).metadata(metaDataMap)
    //           .context(eventContext).build();
      
              internalEventProducer.sendEvent(event);
  }

  private String buildEventContext(String prefix, String context) {
    if (prefix==null && context==null) return "*";
    if (prefix==null && context!=null) return context;
    if (prefix!=null && context==null) return prefix;
    if (prefix!=null && context!=null) return prefix + "." + context;
    return null; // should never get here
  }

  public void error(GatewayErrorEventDTO errorEventDTO) {
    String eventContext = buildEventContext(contextPrefix, errorEventDTO.getContext());
    errorEventDTO.setContext(eventContext);

    internalEventProducer.sendErrorEvent(errorEventDTO);
  }

// ------- deprecated

  @Deprecated
  public void addEventTypes(String[] et) {
  }

  @Deprecated
  public void addErrorEventTypes(String[] et) {
  }

  @Deprecated
  public void triggerEvent(String caseId, String eventType) {
    triggerEvent(caseId, eventType, new String[0]);
  }

  @Deprecated
  public void triggerEvent(String caseId, String eventType, String... metadata) {
    Map<String, String> metaDataMap = createMetaDataMap(metadata);
          GatewayEventDTO gatewayEventDTO = GatewayEventDTO.builder()
              .caseId(caseId).source(source).eventType(eventType).localTime(new Date()).metadata(metaDataMap)
              .context(contextPrefix).build();
    debug(gatewayEventDTO);
  }

  @Deprecated
  public void triggerErrorEvent(Class klass, Exception exception, String message, String caseId, String errorEventType) {
    triggerErrorEvent(klass, exception, message, caseId, errorEventType, new String[0]);
  }

  @Deprecated
  public void triggerErrorEvent(Class klass, String message, String caseId, String errorEventType) {
    triggerErrorEvent(klass, null, message, caseId, errorEventType, new String[0]);
  }

  @Deprecated
  public void triggerErrorEvent(Class klass, String message, String caseId, String errorEventType, String... metadata) {
    triggerErrorEvent(klass, null, message, caseId, errorEventType, metadata);
  }

  @Deprecated
  public void triggerErrorEvent(Class klass, Exception exception, String message, String caseId, String errorEventType, String... metadata) {
    Map <String, String> metaDataMap = createMetaDataMap(metadata);
    GatewayErrorEventDTOBuilder builder = GatewayErrorEventDTO.builder()
        .className(klass.getName()).exceptionName((exception != null) ? exception.getClass().getName() : "<NONE>").message(message)
        .caseId(caseId).errorEventType(errorEventType).source(source).localTime(new Date()).metadata(metaDataMap).context(contextPrefix);

      builder.errorEventType(errorEventType);
    error(builder.build());
  }

  private Map<String, String> createMetaDataMap(String... metadata) {
    int i = 0;
    Map<String, String> metadataMap = new HashMap<>();
    while (i< metadata.length) {
      String key = metadata[i];
      i++;
      if (i<metadata.length) {
        String value = (metadata[i]!=null)?metadata[i]:"null";
        metadataMap.put(key, value);
        i++;
      }
    }
    return metadataMap;
  }

}
