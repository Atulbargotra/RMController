package dev.atul.rmcontroller.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.atul.rmcontroller.dto.RMEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    private void sendMessage(RMEvent rmEvent) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, mapper.writeValueAsString(rmEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessages(List<RMEvent> rmEvents){
        rmEvents.forEach(rmEvent -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                sendMessage(rmEvent);
            });
        });
    }
}