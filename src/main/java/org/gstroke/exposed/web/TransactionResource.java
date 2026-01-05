package org.gstroke.exposed.web;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.gstroke.entity.Transaction;
import org.gstroke.repository.TransactionRepository;
import org.gstroke.request.dto.TransferRequest;
import org.gstroke.response.dto.TransferResponse;
import org.gstroke.service.TransferService;

import java.util.List;
import java.util.UUID;

@Path("/transactions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    TransferService service;

    @Inject
    TransactionRepository transactionRepository;

    @POST
    @RolesAllowed("ROLE_ADMIN")
    @Path("/transfer")
    public TransferResponse transfer(@Valid TransferRequest req) {

        Transaction tx = service.execute(req);

        TransferResponse res = new TransferResponse();
        res.transactionId = tx.id;
        res.status = tx.status.name();
        res.message = "Transfer processed";

        return res;
    }

    @GET
    @Path("/account/{account}")
    @RolesAllowed({"ROLE_USER"})
    public List<Transaction> getByAccount(@PathParam("account") String account) {
        return transactionRepository.findByAccount(account);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER"})
    public Transaction get(@PathParam("id") UUID id) {
        Transaction tx = transactionRepository.findById(id);
        if (tx == null) throw new NotFoundException();
        return tx;
    }
}