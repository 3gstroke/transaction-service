package org.gstroke.health;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class TransactionReadinessCheck implements HealthCheck {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (var c = dataSource.getConnection()) {
            return HealthCheckResponse.up("transaction-db-ready");
        } catch (Exception e) {
            return HealthCheckResponse.down("transaction-db-down");
        }
    }
}