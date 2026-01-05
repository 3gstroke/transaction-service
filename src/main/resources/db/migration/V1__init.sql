-- 1. Crear schema
CREATE SCHEMA IF NOT EXISTS transaction_db;

-- 2. Habilitar UUID generator
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 3. Crear tabla transactions
CREATE TABLE transaction_db.transactions (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    from_account VARCHAR(30) NOT NULL,
    to_account VARCHAR(30) NOT NULL,
    amount NUMERIC(15,2) NOT NULL CHECK (amount > 0),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    correlation_id VARCHAR(255),
    CONSTRAINT pk_transactions PRIMARY KEY (id)
);

-- 4. Índices útiles
CREATE INDEX idx_transactions_from_account
    ON transaction_db.transactions(from_account);

CREATE INDEX idx_transactions_to_account
    ON transaction_db.transactions(to_account);

CREATE INDEX idx_transactions_status
    ON transaction_db.transactions(status);
INSERT INTO transaction_db.transactions
(from_account, to_account, amount, created_at, status, description)
VALUES
('ACC-101', 'ACC-201', 100.00, NOW(), 'COMPLETED', 'Pago servicios'),
('ACC-102', 'ACC-202', 200.00, NOW(), 'FAILED', 'Cuenta destino inactiva'),
('ACC-103', 'ACC-203', 50.00, NOW(), 'COMPLETED', 'Transferencia interna');