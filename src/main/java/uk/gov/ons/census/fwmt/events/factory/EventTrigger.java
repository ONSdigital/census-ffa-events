package uk.gov.ons.census.fwmt.events.factory;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import lombok.Getter;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

public class EventTrigger {
	
	private GatewayEventManager gatewayEventManager;
	
	private String source; // FSDR-SERVICE
	
	private Class klass;

	private String topic;

	public EventTrigger(GatewayEventManager gatewayEventManager, String source, Class klass, String topic) {
		this.gatewayEventManager = gatewayEventManager;
		// TODO Auto-generated constructor stub
		this.source = source;
		this.klass = klass;
		this.topic = topic;
	}

	public EventBuilder test(String id) {
		EventBuilder eb = new EventBuilder() {
			
			@Override
			public void send() {
				  String theId = ((!Strings.isNullOrEmpty(getId()))?id:"<N/A>");
				  String fullTopic = getTopic() + ((!Strings.isNullOrEmpty(getSubTopic()))?"."+getSubTopic():"");
				  meta("source", getSource(),  "topic", fullTopic, "className", getKlass().getName());
			      gatewayEventManager.triggerEvent(theId, getEventType(), getMetaDataArray());
			}
		};
		
		eb.source(source).klass(klass).topic(topic).id(id);
		return eb;
		
	}

	public ErrorEventBuilder error(String id) {
		ErrorEventBuilder eeb = new ErrorEventBuilder() {
			
			@Override
			public void send() {
				  String theId = ((!Strings.isNullOrEmpty(getId()))?id:"<N/A>");
				  String fullTopic = "ERROR." + getTopic() + ((!Strings.isNullOrEmpty(getSubTopic()))?"."+getSubTopic():"");
				  meta("source", source,  "topic", fullTopic);
				  gatewayEventManager.triggerErrorEvent(getKlass(), getException(), getMessage(), theId, getErrorEventType(), getMetaDataArray());
			}
		};
		
		eeb.source(source).klass(klass).topic(topic).id(id);
		return eeb;
		
	}
	
	@Getter
	public static abstract class EventBuilder{
		  private String id;
		  private String source; // FSDR-SERVICE
		  private String eventType; //
		  private String topic;  // routing-key
		  private String subTopic;  // routing-key
		  private Class klass;
//		  private Date localTime;
		  private List<String> metadata = new ArrayList<>();

		  public EventBuilder id(String id) {
			this.id = id;	
			return this;
		  }

		  public EventBuilder source(String source) {
			this.source = source;	
			return this;
		  }

		  public EventBuilder eventType(String eventType) {
			this.eventType = eventType;	
			return this;
		  }

		  public EventBuilder topic(String topic) {
			this.topic = topic;	
			return this;
		  }

		  public EventBuilder subTopic(String subTopic) {
			this.subTopic = subTopic;	
			return this;
		  }

		  public EventBuilder klass(Class klass) {
			this.klass = klass;	
			return this;
		  }

//		  public EventBuilder localTime(Date localTime) {
//			this.localTime = localTime;	
//			return this;
//		  }
		  
		  public EventBuilder meta(String key, String value) {
			  metadata.add(key);
			  metadata.add(value);
			  return this;
		  }
		  
		  public EventBuilder meta(String... data) {
			  for (int i=0; i<data.length ; i++) {
				  metadata.add(data[i]);

			 }
			  return this;
		  }
		  
		  public abstract void send() ;
		  
		  String[] getMetaDataArray() {
			  return metadata.toArray(new String[0]);
		  }
	}

	@Getter
	public static abstract class ErrorEventBuilder{
	  private String id;
	  private String source;
	  private String errorEventType;
//	  private Date localTime;
	  private String topic;
	  private String subTopic;  // routing-key
	  private List<String> metadata = new ArrayList<>();
	  private Class klass;
	  private Exception exception;
	  private String message;

	  public ErrorEventBuilder id(String id) {
		this.id = id;	
		return this;
	  }

	  public ErrorEventBuilder source(String source) {
		this.source = source;	
		return this;
	  }

	  public ErrorEventBuilder errorEventType(String errorEventType) {
		this.errorEventType = errorEventType;	
		return this;
	  }

	  public ErrorEventBuilder topic(String topic) {
		this.topic = topic;	
		return this;
	  }

	  public ErrorEventBuilder subTopic(String subTopic) {
		this.subTopic = subTopic;	
		return this;
	  }

	  public ErrorEventBuilder klass(Class klass) {
		this.klass = klass;	
		return this;
	  }

	  public ErrorEventBuilder exception(Exception exception) {
		this.exception = exception;	
		return this;
	  }

	  public ErrorEventBuilder message(String message) {
		this.message = message;	
		return this;
	  }

//	  public EventBuilder localTime(Date localTime) {
//		this.localTime = localTime;	
//		return this;
//	  }
	  
	  public ErrorEventBuilder meta(String key, String value) {
		  metadata.add(key);
		  metadata.add(value);
		  return this;
	  }
	  
	  public ErrorEventBuilder meta(String... data) {
		  for (int i=0; i<data.length ; i++) {
			  metadata.add(data[i]);
		 }
		  return this;
	  }

	  String[] getMetaDataArray() {
		  return metadata.toArray(new String[0]);
	  }
	  
	  public abstract void send() ;
	}
	
}
