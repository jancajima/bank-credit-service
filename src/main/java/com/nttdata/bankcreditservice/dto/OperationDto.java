package com.nttdata.bankcreditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dto Operation.
 */
@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
public class OperationDto {

    private String accountId;
    private float amount;
    private String debtId;
    private String creditId;

}
