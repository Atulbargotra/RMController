package dev.atul.rmcontroller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class RMRequest {
    private String eventName;
    private String eventReason;
    private String eventDescription;
    private String eventType;
    private List<Metadata> metadata;

}
