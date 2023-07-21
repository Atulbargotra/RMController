package dev.atul.rmcontroller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMEvent {
    private String requestId;
    private String eventName;
    private String eventReason;
    private String eventDescription;
    private String eventType;
    private Metadata metadata;
}
