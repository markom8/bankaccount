package com.whitebox.bankaccount.exception;

import com.whitebox.bankaccount.controller.dto.BankAccountCreationDTO;

public class BankAccountCreationException extends Exception {

    public BankAccountCreationException(BankAccountCreationDTO bankAccountCreationDTO) {
        super("Bank account creation command has invalid paramethers. Parameters = " + bankAccountCreationDTO.toString());
    }
}
