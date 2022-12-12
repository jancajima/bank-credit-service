package com.nttdata.bankcreditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto Transaction.
 */
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class TransactionDto {
    //Date of the transaction
    private String transactionDate;
    //Amount of the transaction
    private float amount;
    //Transaction's type
    private String type;
    //Associated Customer's ID
    private String idCustomer;
    //Associated account's ID
    private String idAccount;
    //Associated account's amount after transaction
    private float accountAmount;
}
