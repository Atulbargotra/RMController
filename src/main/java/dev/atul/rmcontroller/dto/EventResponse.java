package dev.atul.rmcontroller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventResponse {
    private String requestId;
    private String responseStatus;
    private String errorMessage;
}
