package org.gstroke.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.gstroke.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntityBase {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "from_account", nullable = false)
    public String fromAccount;

    @Column(name = "to_account", nullable = false)
    public String toAccount;

    @Column(nullable = false)
    public BigDecimal amount;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TransactionStatus status;

    public String description;

    @Column(name = "correlation_id")
    public String correlationId;

}