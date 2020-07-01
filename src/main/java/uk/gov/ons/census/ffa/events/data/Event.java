package uk.gov.ons.census.ffa.events.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Serializable {
  private String caseId;
  private String source;
  private String eventType;
  private String context;
  private Date localTime;
  private Map<String, String> metadata;
}
