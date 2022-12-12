package com.nttdata.bankcreditservice.service.impl;

import com.nttdata.bankcreditservice.document.BankDebt;
import com.nttdata.bankcreditservice.dto.OperationDto;
import com.nttdata.bankcreditservice.dto.TransactionDto;
import com.nttdata.bankcreditservice.repository.BankDebtRepository;
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
 * Bank Debt Service Implementation.
 */
@Service
public class BankDebtServiceImpl implements BankDebtService {

    @Autowired
    private BankDebtRepository bankDebtRepository;

    @Autowired
    private WebClient.Builder webClient;

    @Override
    public Flux<BankDebt> findAll() {
        return this.bankDebtRepository.findAll();
    }

    @Override
    public Mono<BankDebt> register(BankDebt bankDebt) {
        return this.bankDebtRepository.save(bankDebt);
    }

    @Override
    public Mono<BankDebt> update(BankDebt bankDebt) {
        return this.bankDebtRepository.save(bankDebt);
    }

    @Override
    public Mono<BankDebt> findById(String id) {
        return this.bankDebtRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        return this.bankDebtRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return this.bankDebtRepository.existsById(id);
    }

    @Override
    public Flux<BankDebt> findByCustomerId(String customerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.bankDebtRepository.findByCustomerId(customerId)
            .filter(a -> a.getBalance() > 0 && LocalDate.from(LocalDate.now())
            .compareTo(LocalDate.parse(a.getPaymentDate(), formatter)) > 0);
    }

    @Override
    public Mono<BankDebt> payDebt(OperationDto transaction) {
        return findById(transaction.getDebtId()).flatMap(x -> {
            float newAmount = x.getBalance() - transaction.getAmount();
            x.setBalance(newAmount);
            TransactionDto t = new TransactionDto(LocalDate.now().toString(),
            transaction.getAmount(), "debt payment", x.getCustomerId(),
            transaction.getAccountId(), newAmount);
            return this.webClient.build().post().uri("/transaction/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(t), TransactionDto.class)
                .retrieve()
                .bodyToMono(TransactionDto.class)
                .flatMap(y -> update(x));
        });
    }


}
