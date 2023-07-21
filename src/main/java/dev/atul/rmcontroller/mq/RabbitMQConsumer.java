package dev.atul.rmcontroller.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.atul.rmcontroller.dto.EventResponse;
import dev.atul.rmcontroller.dto.RMEventLookup;
import dev.atul.rmcontroller.dto.RMEventResponse;
import dev.atul.rmcontroller.dto.RMResponse;
import dev.atul.rmcontroller.repository.RMEventLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class RabbitMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final RMEventLookupRepository repository;

    private final Map<String, RMResponse> responseMap = new HashMap<>();

    public RabbitMQConsumer(RMEventLookupRepository repository) {
        this.repository = repository;
    }

    private Map<String, CountDownLatch> latchMap = new HashMap<>();

    @RabbitListener(queues = {"${rabbitmq.queue.response.name}"})
    public void consume(String message){
        ObjectMapper mapper = new ObjectMapper();
        try {
            RMEventResponse rmEventResponse = mapper.readValue(message, RMEventResponse.class);
            EventResponse eventResponse = new EventResponse();
            eventResponse.setResponseStatus(rmEventResponse.getStatus());
            eventResponse.setRequestId(rmEventResponse.getRequestId());
            eventResponse.setErrorMessage(rmEventResponse.getErrorMessage());

            Optional<RMEventLookup> byId = repository.findById(rmEventResponse.getRequestId());


            if(responseMap.containsKey(rmEventResponse.getRequestId())){
                responseMap.get(rmEventResponse.getRequestId()).getEventResponses().add(eventResponse);
                if(byId.isPresent()){
                    RMEventLookup rmEventLookup = byId.get();
                    rmEventLookup.setCurrentEvents(rmEventLookup.getCurrentEvents() + 1);

                    if(rmEventLookup.getCurrentEvents() == rmEventLookup.getTotalEvents()){
                        //TODO: Send Response
                        System.out.println(responseMap.get(rmEventResponse.getRequestId()));
                        latchMap.get(rmEventResponse.getRequestId()).countDown();
                    }

                    repository.save(rmEventLookup);
                }
            }
            else{
                RMResponse rmResponse = new RMResponse();
                List<EventResponse> eventResponses = new ArrayList<>();
                eventResponses.add(eventResponse);
                rmResponse.setEventResponses(eventResponses);
                responseMap.put(rmEventResponse.getRequestId(), rmResponse);

                if(byId.isPresent()){
                    RMEventLookup rmEventLookup = byId.get();
                    rmEventLookup.setCurrentEvents(rmEventLookup.getCurrentEvents() + 1);

                    if(rmEventLookup.getCurrentEvents() == rmEventLookup.getTotalEvents()){
                        //TODO: Send Response
                        System.out.println(responseMap.get(rmEventResponse.getRequestId()));
                        latchMap.get(rmEventResponse.getRequestId()).countDown();

                    }
                    repository.save(rmEventLookup);

                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public RMResponse getAggregatedResponses(String requestId) throws InterruptedException {
        if(!latchMap.containsKey(requestId)){
            latchMap.put(requestId, new CountDownLatch(1));
        }
        latchMap.get(requestId).await();
        return responseMap.get(requestId);
    }
}
