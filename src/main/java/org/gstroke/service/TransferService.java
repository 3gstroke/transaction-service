package org.gstroke.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.gstroke.entity.Transaction;
import org.gstroke.enums.TransactionStatus;
import org.gstroke.event.TransactionEventProducer;
import org.gstroke.event.dto.TransactionEvent;
import org.gstroke.exception.BusinessException;
import org.gstroke.exception.ErrorCode;
import org.gstroke.proxy.AccountClient;
import org.gstroke.proxy.CustomerClient;
import org.gstroke.proxy.dto.AccountResponse;
import org.gstroke.request.dto.TransferRequest;

import java.time.LocalDateTime;

@ApplicationScoped
public class TransferService {

    @Inject
    @RestClient
    AccountClient accountClient;

    @Inject
    @RestClient
    CustomerClient customerClient;

    @Inject
    TransactionEventProducer producer;

    @Transactional
    public Transaction execute(TransferRequest req) {

        if (req.fromAccount.equals(req.toAccount)) {
            throw new IllegalArgumentException("Accounts must be different");
        }

        AccountResponse from = accountClient.getAccount(req.fromAccount);
        AccountResponse to = accountClient.getAccount(req.toAccount);

        validateCustomerOrFail(from.customerId);
        validateCustomerOrFail(to.customerId);

        if (!from.status.equals("ACTIVE") || !to.status.equals("ACTIVE")) {
            throw new BadRequestException("Inactive account");
        }

        if (from.balance.compareTo(req.amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        if (!accountClient.validateBalance(req.fromAccount, req.amount)) {
            throw new IllegalStateException("Insufficient balance");
        }

        Transaction tx = new Transaction();
        tx.fromAccount = req.fromAccount;
        tx.toAccount = req.toAccount;
        tx.amount = req.amount;
        tx.createdAt = LocalDateTime.now();
        tx.status = TransactionStatus.PENDING;
        tx.description = "Transferencia iniciada";

        //repository.persist(tx);
        tx.persist();

        try {

            //accountClient.getAccount(req.fromAccount);


            tx.status = TransactionStatus.COMPLETED;
            tx.description = "Transferencia completa";

            producer.publishCompleted(mapEvent(tx));

        } catch (Exception e) {

            tx.status = TransactionStatus.FAILED;
            producer.publishFailed(mapEvent(tx));
            throw e;
        }

        return tx;
    }

    private TransactionEvent mapEvent(Transaction tx) {
        TransactionEvent event = new TransactionEvent();
        event.transactionId = tx.id;
        event.fromAccount = tx.fromAccount;
        event.toAccount = tx.toAccount;
        event.amount = tx.amount;
        event.status = tx.status.name();
        event.correlationId = tx.correlationId;
        return event;
    }

    public void validateCustomerOrFail(Long customerId) {

        Response response = customerClient.validateCustomer(customerId);

        if (response.getStatus() == 404) {
            throw new BusinessException(
                    ErrorCode.CUSTOMER_NOT_FOUND,
                    "Customer does not exist"
            );
        }

        if (response.getStatus() == 409) {
            throw new BusinessException(
                    ErrorCode.CUSTOMER_INACTIVE,
                    "Customer is inactive"
            );
        }

        if (response.getStatus() != 200) {
            throw new BusinessException(
                    ErrorCode.TRANSACTION_FAILED,
                    "Customer validation failed"
            );
        }
    }
}