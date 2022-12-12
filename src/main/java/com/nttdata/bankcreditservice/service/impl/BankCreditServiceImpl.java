package com.nttdata.bankcreditservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nttdata.bankcreditservice.document.BankCredit;
import com.nttdata.bankcreditservice.dto.OperationDto;
import com.nttdata.bankcreditservice.dto.TransactionDto;
import com.nttdata.bankcreditservice.producer.KafkaPayCreditProducer;
import com.nttdata.bankcreditservice.repository.BankCreditRepository;
import com.nttdata.bankcreditservice.service.BankCreditService;
import com.nttdata.bankcreditservice.service.BankDebtService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Bank Credit Service Implementation.
 */
@Service
public class BankCreditServiceImpl implements BankCreditService {

    @Autowired
    private BankCreditRepository bankCreditRepository;

    @Autowired
    private BankDebtService bankDebtService;

    @Autowired
    private WebClient.Builder webClient;

    private final KafkaPayCreditProducer kafkaPayCreditProducer;

    @Autowired
    public BankCreditServiceImpl(KafkaPayCreditProducer kafkaPayCreditProducer) {
        this.kafkaPayCreditProducer = kafkaPayCreditProducer;
    }

    @Override
    public Flux<BankCredit> findAll() {
        return this.bankCreditRepository.findAll();
    }

    @Override
    public Mono<BankCredit> register(BankCredit bankCredit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        bankCredit.setCreationDate(LocalDate.now().format(formatter));
        return this.bankCreditRepository.save(bankCredit);
    }

    @Override
    public Mono<BankCredit> update(BankCredit bankCredit) {
        return this.bankCreditRepository.save(bankCredit);
    }

    @Override
    public Mono<BankCredit> findById(String id) {
        return this.bankCreditRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.bankCreditRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return this.bankCreditRepository.existsById(id);
    }

    @Override
    public Mono<BankCredit> payCredit(OperationDto transaction) {
        return findById(transaction.getCreditId()).flatMap(x -> {
            float newAmount = x.getAmount() + transaction.getAmount();
            if (newAmount <= x.getCredit()) {
                TransactionDto t = new TransactionDto(LocalDate.now().toString(),
                transaction.getAmount(), "credit payment", x.getCustomerId(),
                transaction.getAccountId(), newAmount);
                x.setAmount(newAmount);
                return this.webClient.build().post().uri("/transaction/")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(t), TransactionDto.class)
                    .retrieve()
                    .bodyToMono(TransactionDto.class)
                    .flatMap(y -> update(x));
            } else {
                return Mono.empty();
            }
        });
    }

    @Override
    public Mono<BankCredit> chargeCredit(OperationDto transaction) {
        return findById(transaction.getAccountId()).flatMap(x -> {
            float newAmount = x.getAmount() - transaction.getAmount();
            if (newAmount >= 0) {
                TransactionDto t = new TransactionDto(LocalDate.now().toString(),
                transaction.getAmount(), "credit charge", x.getCustomerId(),
                transaction.getAccountId(), newAmount);
                x.setAmount(newAmount);
                return this.webClient.build().post().uri("/transaction/")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(t), TransactionDto.class)
                    .retrieve()
                    .bodyToMono(TransactionDto.class)
                    .flatMap(y -> update(x));
            } else {
                return Mono.empty();
            }
        });
    }

    @Override
    public Flux<BankCredit> findByCustomerId(String customerId) {
        return this.bankCreditRepository.findByCustomerId(
                customerId);
    }

    @Override
    public Mono<BankCredit> findByNumberCredit(String numberCredit) {
        return this.bankCreditRepository.findByNumberCredit(numberCredit);
    }

    @Override
    public Mono<BankCredit> payCreditKafka(OperationDto transaction) {
        return findById(transaction.getCreditId()).flatMap(x -> {
            float newAmount = x.getAmount() + transaction.getAmount();
            if (newAmount <= x.getCredit()) {
                TransactionDto t = new TransactionDto(LocalDate.now().toString(),
                transaction.getAmount(), "credit payment", x.getCustomerId(),
                transaction.getAccountId(), newAmount);
                x.setAmount(newAmount);
                try {
                    kafkaPayCreditProducer.sendMessagePayCredit(transaction);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return update(x);
            } else {
                return Mono.empty();
            }
        });
    }

}
