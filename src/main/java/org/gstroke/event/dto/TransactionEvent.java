package org.gstroke.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionEvent {
    public UUID transactionId;
    public String fromAccount;
    public String toAccount;
    public BigDecimal amount;
    public String status;
    public LocalDateTime timestamp;
    public String correlationId;
}
