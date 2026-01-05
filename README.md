# transaction-service

## 📌 Descripción

`transaction-service` es el **microservicio orquestador de transferencias bancarias**. Se encarga de validar clientes y cuentas, registrar la transacción, aplicar reglas de negocio y publicar el resultado (éxito o fallo) mediante eventos Kafka.

Es el **punto central del flujo transaccional**, asegurando consistencia y trazabilidad.

---

## 🧱 Stack Tecnológico

* **Java:** 21
* **Framework:** Quarkus 3.27.1
* **Persistencia:** Hibernate ORM + Panache
* **Base de datos:** PostgreSQL
* **Migraciones:** Flyway
* **Mensajería:** Apache Kafka (SmallRye Reactive Messaging)
* **Seguridad:** JWT (RS256)
* **Observabilidad:** Health, Metrics (Prometheus), JSON Logging
* * **Docker Compose:** PostgreSQL + RedPanda (Kafka)en Raspberrypi 5

---

## ⚙️ Configuración Principal

## Docker Compose
```
* docker-compose.yaml *
services:
    postgres_db:
      image: postgres:16
      container_name: postgres_db
      restart: always
      environment:
        POSTGRES_USER: gstroke
        POSTGRES_PASSWORD: gstroke24
        POSTGRES_DB: dev_gstroke_db
      volumes:
        - postgres_data:/var/lib/postgresql/data
      ports:
        - "5432:5432"
      healthcheck:
        test: ["CMD-SHELL", "pg_isready -U gstroke"]
        interval: 10s
        timeout: 5s
        retries: 5
        
   redpanda:
     image: redpandadata/redpanda:v24.1.3
     container_name: redpanda
     command:
       - redpanda
       - start
       - --smp
       - "1"
       - --memory
       - "1G"
       - --overprovisioned
       - --node-id
       - "0"
       - --check=false
       - --kafka-addr
       - PLAINTEXT://0.0.0.0:9092
       - --advertise-kafka-addr
       - PLAINTEXT://192.168.18.31:9092
     ports:
       - "9092:9092"
       - "9644:9644"
```
* Ejecutar sudo nano docker compose up -d
* Verificar con docker ps

---

## 🗄️ Base de Datos

**Motor:** PostgreSQL
**Base:** `dev_gstroke_db`
**Schema:** `transaction_db`

### Configuración relevante

* El esquema se **valida** al iniciar (`validate`)
* Las migraciones se ejecutan automáticamente con Flyway
* No se crean ni alteran tablas fuera de Flyway

```properties
quarkus.hibernate-orm.database.default-schema=transaction_db
quarkus.hibernate-orm.schema-management.strategy=validate
quarkus.flyway.migrate-at-start=true
```

---

## 🔐 Seguridad (JWT)

* Algoritmo: **RS256**
* Issuer esperado: `bank-api`
* Clave privada local (`privateKey.pem`) para generación de tokens

### Reglas de acceso

| Endpoint          | Seguridad                     |
| ----------------- | ----------------------------- |
| `/jwt/*`          | Público (generación de token) |
| `/transactions/*` | Requiere JWT válido           |

```properties
quarkus.http.auth.permission.secured.paths=/transactions/*
quarkus.http.auth.permission.secured.policy=authenticated
```

---

## 🔁 Integración entre Microservicios

### REST Clients

#### customer-service (puerto 8081)

* Validación de existencia del cliente
* Evita transferencias hacia/desde clientes inexistentes

#### account-service (puerto 8082)

* Validación de cuentas
* Débito y crédito indirecto vía eventos

```properties
quarkus.rest-client.customer-service.url=http://localhost:8081/
quarkus.rest-client.account-service.url=http://localhost:8082/
```

---

## 📬 Mensajería Kafka

### Topics publicados

| Topic                    | Descripción               |
| ------------------------ | ------------------------- |
| `transactions.completed` | Transferencias exitosas   |
| `transactions.failed`    | Transferencias rechazadas |

* ACKs configurados en `all`
* Serialización JSON con Jackson

```properties
mp.messaging.outgoing.transactions-completed-out.topic=transactions.completed
mp.messaging.outgoing.transactions-failed-out.topic=transactions.failed
```

---

## 🔄 Flujo de una Transferencia

1. Cliente invoca `POST /transactions`
2. Se valida el **JWT**
3. Se valida existencia del **customer**
4. Se valida la **cuenta origen/destino**
5. Se registra la transacción en BD
6. Se publica evento:

    * ✅ `transactions.completed`
    * ❌ `transactions.failed`

---

## 🧪 Testing

* Tests unitarios con JUnit 5
* Tests REST con Rest-Assured
* Flyway y PostgreSQL reales (no H2)

---

## 📊 Observabilidad

### Health

```
GET /q/health
```

### Métricas (Prometheus)

```
GET /q/metrics
```

### Logging

* Formato JSON
* CorrelationId soportado

---

## ▶️ Ejecución Local

```bash
./mvnw quarkus:dev
```

Puerto por defecto: **8083**

---

## 🧠 Decisiones de Diseño

* El `transaction-service` **no modifica saldos directamente**
* Usa eventos para desacoplar lógica financiera
* Garantiza consistencia eventual
* Centraliza las reglas de negocio

---

## 📎 Relación con otros Microservicios

| Servicio            | Rol                         |
| ------------------- | --------------------------- |
| customer-service    | Validación de clientes      |
| account-service     | Gestión de cuentas y saldos |
| transaction-service | Orquestación y consistencia |

---

🚀 **Este microservicio es el núcleo del sistema de transferencias bancarias distribuido.**
