# census-ffa-events
repo for handling events and creating events queue.

# Quick Start

## Triggering Events

For any class that want to log events add this a class. The second parameter is the Routing Key for any events sent from the class.

```
  @Autowired
  private EventTriggerFactory eventTriggerFactory;
  
  private EventTrigger eventTrigger;
  
  @PostConstruct
  public void setUpEventTrigger() {
	  eventTrigger = eventTriggerFactory.createEventTrigger(this.getClass(), "ACTION.RECEIVED.GSUITE");
  }
```


To send an event run add the following code

```
	eventTrigger.test(gSuiteAction.getEmployee().getUniqueEmployeeId()).eventType(RECEIVED_GSUITE_ACTION_MESSAGE).meta("Action", gSuiteAction.getAction().name()).send();
```
 and for errors
 
 ```
       eventTrigger.error(employee.getUniqueEmployeeId()).message(msg).errorEventType(FAILED_GSUITE_SEND).exception(e).send();
 ```
 
 
 This is an initial checkin of the code and is unfinished.
 
 The log levels are still being defined and are may change. Current thinking is 
 
 operational - events needed by the application
 dashboard - events used for the dashboarding
 test - events used for tests
 error - events depicting errors
 
 currently on test and error have been created.
 
## Listening Events

This library is built on Topic queues, use RabbitMQ/Spring-AMQP on how to do this.
 