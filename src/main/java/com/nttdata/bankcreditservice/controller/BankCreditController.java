package com.nttdata.bankcreditservice.controller;

import com.nttdata.bankcreditservice.document.BankCredit;
import com.nttdata.bankcreditservice.dto.OperationDto;
import com.nttdata.bankcreditservice.producer.KafkaStringProducer;
import com.nttdata.bankcreditservice.service.BankCreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller of Bank Credit.
 */
@RestController
@RequestMapping("/bankCredit")
public class BankCreditController {

    @Autowired
    private BankCreditService bankCreditService;

    private final KafkaStringProducer kafkaStringProducer;

    @Autowired
    public BankCreditController(KafkaStringProducer kafkaStringProducer) {
        this.kafkaStringProducer = kafkaStringProducer;
    }

    //Method to get all the bank credits
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Flux<BankCredit> findAll() {
        return bankCreditService.findAll();
    }

    //Method to insert a new bank credit
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BankCredit> register(@RequestBody BankCredit bankCredit) {
        return bankCreditService.register(bankCredit);
    }

    //Method to update a bank credit
    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BankCredit> update(@RequestBody BankCredit bankCredit) {
        return bankCreditService.update(bankCredit);
    }

    //Method to get a bank credit by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BankCredit> findById(@PathVariable("id") String id) {
        return bankCreditService.findById(id);
    }

    //Method to delete a bank credit
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> delete(@PathVariable("id") String id) {
        return bankCreditService.delete(id);
    }

    //Method to pay credit
    @PutMapping("/payCredit")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BankCredit> payCredit(@RequestBody OperationDto transaction) {
        return bankCreditService.payCredit(transaction);
    }

    //Method to charge credit
    @PutMapping("/chargeCredit")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BankCredit> chargeCredit(@RequestBody OperationDto transaction) {
        return bankCreditService.chargeCredit(transaction);
    }

    //Method to get a bank credit by clientId
    @GetMapping("/creditByCustomerId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<BankCredit> findByCustomerId(@PathVariable("id") String customerId) {
        return bankCreditService.findByCustomerId(customerId);
    }

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
        this.kafkaStringProducer.sendMessage(message);
    }

    //Method to pay credit to Kafka
    @PutMapping("/payCreditKafka")
    @ResponseStatus(HttpStatus.OK)
    public Mono<BankCredit> payCreditKafka(@RequestBody OperationDto transaction) {
        return bankCreditService.payCreditKafka(transaction);
    }

}
