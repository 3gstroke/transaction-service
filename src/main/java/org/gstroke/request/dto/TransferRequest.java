package org.gstroke.request.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {

    @NotBlank
    public String fromAccount;

    @NotBlank
    public String toAccount;

    @NotNull
    @DecimalMin(value = "0.01")
    public BigDecimal amount;
}