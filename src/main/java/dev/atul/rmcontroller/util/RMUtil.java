package dev.atul.rmcontroller.util;

import dev.atul.rmcontroller.dto.Metadata;
import dev.atul.rmcontroller.dto.RMEvent;
import dev.atul.rmcontroller.dto.RMEventLookup;
import dev.atul.rmcontroller.dto.RMRequest;
import dev.atul.rmcontroller.repository.RMEventLookupRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class RMUtil {
    private final RMEventLookupRepository repository;

    public RMUtil(RMEventLookupRepository repository) {
        this.repository = repository;
    }

    public List<RMEvent> getRMEvents(String requestId, RMRequest request){
        List<RMEvent> events = new ArrayList<>();
        RMEventLookup rmEventLookup = new RMEventLookup(requestId, 0, request.getMetadata().size());
        repository.save(rmEventLookup);
        for(Metadata metadata: request.getMetadata()){
            RMEvent rmEvent = new RMEvent();
            rmEvent.setRequestId(requestId);
            rmEvent.setEventReason(request.getEventReason());
            rmEvent.setEventName(request.getEventName());
            rmEvent.setEventType(request.getEventType());
            rmEvent.setEventDescription(request.getEventDescription());
            rmEvent.setMetadata(metadata);
            events.add(rmEvent);
        }
        return events;
    }
}
