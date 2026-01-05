package org.gstroke.event;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.gstroke.event.dto.TransactionEvent;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static io.quarkus.arc.ComponentsProvider.LOG;

@ApplicationScoped
public class TransactionEventProducer {

    @Inject
    @Channel("transactions-completed-out")
    Emitter<TransactionEvent> completedEmitter;

    @Inject
    @Channel("transactions-failed-out")
    Emitter<TransactionEvent> failedEmitter;

    public void publishCompleted(TransactionEvent event) {

        String correlationId = (String) MDC.get("X-Correlation-Id");

        event.correlationId = correlationId;
        LOG.infof("Processing transaction %s", correlationId);

        completedEmitter.send(Message.of(event)
                .addMetadata(OutgoingKafkaRecordMetadata
                                .builder()
                                .addHeaders(new RecordHeader("X-Correlation-Id",
                                        correlationId.getBytes(StandardCharsets.UTF_8)))
                                .build()));

    }

    public void publishFailed(TransactionEvent event) {
        event.timestamp = LocalDateTime.now();
        failedEmitter.send(event);
    }

}
