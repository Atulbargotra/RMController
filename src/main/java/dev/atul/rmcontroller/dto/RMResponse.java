package dev.atul.rmcontroller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@ToString
public class RMResponse {
    private String responseStatus;
    private List<EventResponse> eventResponses;

}
