package org.gstroke.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.gstroke.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
public class TransactionRepository
        implements PanacheRepositoryBase<Transaction, UUID> {

    public Optional<Transaction> findByTransactionId(UUID id) {
        return find("transactionId", id).firstResultOptional();
    }

    public List<Transaction> findByAccount(String account) {
        return list("fromAccount = ?1 OR toAccount = ?1", account, account);
    }

}