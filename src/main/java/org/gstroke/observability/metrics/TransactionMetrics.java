package org.gstroke.observability.metrics;

import io.micrometer.core.instrument.Gauge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@ApplicationScoped
public class TransactionMetrics {

    private final Counter successCounter;
    private final Counter failedCounter;
    private final AtomicReference<Double> totalTransferred;

    @Inject
    public TransactionMetrics(MeterRegistry registry) {

        this.successCounter = Counter.builder("transfers_success_total")
                .description("Transferencias exitosas")
                .register(registry);

        this.failedCounter = Counter.builder("transfers_failed_total")
                .description("Transferencias fallidas")
                .register(registry);

        this.totalTransferred = new AtomicReference<>(0.0);

        Gauge.builder(
                        "total_transferred_amount",
                        totalTransferred,
                        AtomicReference::get
                )
                .description("Monto total transferido")
                .register(registry);
    }

    public void markSuccess(BigDecimal amount) {
        successCounter.increment();
        totalTransferred.updateAndGet(v -> v + amount.doubleValue());
    }

    public void markFailed() {
        failedCounter.increment();
    }
}