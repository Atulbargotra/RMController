package dev.atul.rmcontroller.controller;
import dev.atul.rmcontroller.dto.RMRequest;
import dev.atul.rmcontroller.dto.RMResponse;
import dev.atul.rmcontroller.mq.RabbitMQConsumer;
import dev.atul.rmcontroller.mq.RabbitMQProducer;
import dev.atul.rmcontroller.util.RMUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@ResponseBody
@RequestMapping("/rm/event")
public class RMEventController {
    private final RabbitMQProducer producer;
    private final RabbitMQConsumer consumer;
    private final RMUtil rmUtil;

    public RMEventController(RabbitMQProducer producer, RabbitMQConsumer consumer, RMUtil rmUtil) {
        this.producer = producer;
        this.consumer = consumer;
        this.rmUtil = rmUtil;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RMResponse> postRMEvents(@RequestBody RMRequest request) throws InterruptedException {
        String requestId = UUID.randomUUID().toString();
        producer.sendMessages(rmUtil.getRMEvents(requestId, request));
        RMResponse aggregatedResponses = consumer.getAggregatedResponses(requestId);
        return new ResponseEntity<>(aggregatedResponses, HttpStatus.OK);
    }
}
