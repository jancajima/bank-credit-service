package com.nttdata.bankcreditservice.service;

import com.nttdata.bankcreditservice.document.BankCredit;
import com.nttdata.bankcreditservice.dto.OperationDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Bank Credit Service.
 */
public interface BankCreditService {

    Flux<BankCredit> findAll();

    Mono<BankCredit> register(BankCredit bankCredit);

    Mono<BankCredit> update(BankCredit bankCredit);

    Mono<BankCredit> findById(String id);

    Mono<Void> delete(String id);

    Mono<Boolean> existsById(String id);

    Mono<BankCredit> payCredit(OperationDto transaction);

    Mono<BankCredit> chargeCredit(OperationDto transaction);

    Flux<BankCredit> findByCustomerId(String customerId);

    Mono<BankCredit> findByNumberCredit(String numberCredit);

    Mono<BankCredit> payCreditKafka(OperationDto transaction);
}
