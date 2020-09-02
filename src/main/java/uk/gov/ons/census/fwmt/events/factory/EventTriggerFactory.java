package uk.gov.ons.census.fwmt.events.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Setter;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

@Component
@Setter
public class EventTriggerFactory {
	private String source;
	
	@Autowired
	private GatewayEventManager gatewayEventManager;
	
	public EventTrigger createEventTrigger(Class klass, String topic) {
		return new EventTrigger(gatewayEventManager, source, klass, topic);
	}
}
