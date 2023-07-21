package dev.atul.rmcontroller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMEventResponse {
    private String requestId;
    private String status;
    private String errorMessage;
}
