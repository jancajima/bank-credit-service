package com.nttdata.bankcreditservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bankcreditservice.dto.OperationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer of Pay Credit.
 */
@Component
public class KafkaPayCreditProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPayCreditProducer.class);
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, String> kafkaTemplateCredit;

    public KafkaPayCreditProducer(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        this.kafkaTemplateCredit = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    //Method to send Message when pay credit.
    public void sendMessagePayCredit(OperationDto transaction) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(transaction);

        LOGGER.info("Producing message PayCredit{} ", message);
        this.kafkaTemplateCredit.send("payCredit", message);
    }

}