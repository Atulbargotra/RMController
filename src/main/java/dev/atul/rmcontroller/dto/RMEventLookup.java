package dev.atul.rmcontroller.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "RMEVENTLOOKUP")
public class RMEventLookup {
    @Id
    private String requestId;
    private int currentEvents;
    private int totalEvents;
}
