# Diagram Arsitektur MediTrack

## 1. High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │ Web App  │  │ Mobile   │  │ Admin    │  │ Pharmacy │       │
│  │          │  │ App      │  │ Portal   │  │ Portal   │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
└───────┼─────────────┼─────────────┼─────────────┼──────────────┘
        │             │             │             │
        └─────────────┴─────────────┴─────────────┘
                      │
        ┌─────────────▼─────────────┐
        │      API Gateway          │
        │   (Port: 8080)            │
        │  - Routing                │
        │  - Authentication         │
        │  - Rate Limiting          │
        └─────────────┬─────────────┘
                      │
        ┌─────────────┴─────────────────────────────────┐
        │                                                 │
┌───────▼────────┐                            ┌──────────▼─────────┐
│ Service        │                            │  Config Server     │
│ Registry       │◄───────────────────────────┤  (Port: 8888)      │
│ (Eureka)       │    Service Registration    │                    │
│ (Port: 8761)   │                            └────────────────────┘
└───────┬────────┘
        │
        │ Service Discovery
        │
┌───────┴────────────────────────────────────────────────────────┐
│                     Microservices Layer                         │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │  User    │  │Appointmt │  │   EHR    │  │ Pharmacy │      │
│  │ Service  │  │ Service  │  │ Service  │  │ Service  │      │
│  │ :8081    │  │ :8082    │  │ :8083    │  │ :8084    │      │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘      │
│       │             │             │             │              │
│  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐  ┌────▼─────┐      │
│  │ User DB  │  │ Appt DB  │  │ EHR DB   │  │Pharmacy  │      │
│  │          │  │          │  │          │  │   DB     │      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                     │
│  │Analytics │  │ Payment  │  │Notificat │                     │
│  │ Service  │  │ Service  │  │  Service │                     │
│  │ :8085    │  │ :8086    │  │ :8087    │                     │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘                     │
│       │             │             │                             │
│  ┌────▼─────┐  ┌────▼─────┐      │                             │
│  │Analytics │  │ Payment  │      │                             │
│  │   DB     │  │   DB     │      │                             │
│  └──────────┘  └──────────┘      │                             │
└──────────────────────────────────┼──────────────────────────────┘
                                    │
        ┌───────────────────────────┴───────────────────────────┐
        │                                                         │
┌───────▼────────┐              ┌──────────────┐                 │
│   RabbitMQ     │              │    Redis     │                 │
│ Message Queue  │              │    Cache     │                 │
│  (Port: 5672)  │              │ (Port: 6379) │                 │
└────────────────┘              └──────────────┘                 │
                                                                  │
┌─────────────────────────────────────────────────────────────────┘
│              Infrastructure & Monitoring Layer                  │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │   ELK    │  │Prometheus│  │ Grafana  │  │  Zipkin  │      │
│  │  Stack   │  │          │  │          │  │          │      │
│  │ Logging  │  │ Metrics  │  │Dashboard │  │ Tracing  │      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Service Communication Flow

### 2.1 Synchronous Communication (REST)

```
┌─────────┐
│ Client  │
└────┬────┘
     │ 1. HTTP Request
     ▼
┌─────────────────┐
│  API Gateway    │
│  - Auth Check   │
│  - Rate Limit   │
└────┬────────────┘
     │ 2. Route to Service
     ▼
┌─────────────────┐      ┌─────────────────┐
│ User Service    │◄────►│ Service         │
│                 │      │ Registry        │
└────┬────────────┘      └─────────────────┘
     │ 3. Query Database
     ▼
┌─────────────────┐
│   User DB       │
└─────────────────┘
     │ 4. Return Data
     ▼
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │ 5. HTTP Response
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

### 2.2 Asynchronous Communication (Events)

```
┌──────────────────┐
│  Appointment     │
│  Service         │
└────┬─────────────┘
     │ 1. Publish Event
     │    "AppointmentBooked"
     ▼
┌──────────────────┐
│   RabbitMQ       │
│  Message Queue   │
└────┬─────────────┘
     │
     ├─────────────────────────────────┐
     │                                 │
     │ 2. Subscribe                    │ 2. Subscribe
     ▼                                 ▼
┌──────────────────┐            ┌──────────────────┐
│  Notification    │            │   Analytics      │
│  Service         │            │   Service        │
└────┬─────────────┘            └────┬─────────────┘
     │                               │
     │ 3. Send Email/SMS             │ 3. Update Metrics
     ▼                               ▼
┌──────────────────┐            ┌──────────────────┐
│   Patient        │            │  Analytics DB    │
└──────────────────┘            └──────────────────┘
```

---

## 3. Appointment Booking Flow (End-to-End)

```
┌─────────┐
│ Patient │
└────┬────┘
     │ 1. Book Appointment
     ▼
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │ 2. Authenticate
     ▼
┌─────────────────┐
│  User Service   │
└────┬────────────┘
     │ 3. Verify Token
     ▼
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │ 4. Route Request
     ▼
┌─────────────────┐
│  Appointment    │
│  Service        │
└────┬────────────┘
     │ 5. Check Doctor Availability
     ▼
┌─────────────────┐
│  Appointment DB │
└────┬────────────┘
     │ 6. Create Appointment
     ▼
┌─────────────────┐
│  Appointment    │
│  Service        │
└────┬────────────┘
     │ 7. Publish Event
     │    "AppointmentBooked"
     ▼
┌─────────────────┐
│   RabbitMQ      │
└────┬────────────┘
     │
     ├──────────────────────────┐
     │                          │
     ▼                          ▼
┌─────────────────┐      ┌─────────────────┐
│  Notification   │      │   Analytics     │
│  Service        │      │   Service       │
└────┬────────────┘      └────┬────────────┘
     │                        │
     │ 8. Send Email          │ 8. Update Stats
     ▼                        ▼
┌─────────────────┐      ┌─────────────────┐
│   Patient       │      │  Analytics DB   │
│   (Email)       │      │                 │
└─────────────────┘      └─────────────────┘
```

---

## 4. Data Flow - Prescription Order

```
┌─────────┐
│ Doctor  │
└────┬────┘
     │ 1. Create Prescription
     ▼
┌─────────────────┐
│  EHR Service    │
└────┬────────────┘
     │ 2. Save to DB
     ▼
┌─────────────────┐
│    EHR DB       │
└────┬────────────┘
     │ 3. Publish Event
     │    "PrescriptionCreated"
     ▼
┌─────────────────┐
│   RabbitMQ      │
└────┬────────────┘
     │
     ├──────────────────────────┐
     │                          │
     ▼                          ▼
┌─────────────────┐      ┌─────────────────┐
│  Pharmacy       │      │  Notification   │
│  Service        │      │  Service        │
└────┬────────────┘      └────┬────────────┘
     │                        │
     │ 4. Create Order        │ 4. Notify Patient
     ▼                        ▼
┌─────────────────┐      ┌─────────────────┐
│  Pharmacy DB    │      │   Patient       │
└────┬────────────┘      └─────────────────┘
     │
     │ 5. Patient Orders
     ▼
┌─────────────────┐
│  Pharmacy       │
│  Service        │
└────┬────────────┘
     │ 6. Process Payment
     ▼
┌─────────────────┐
│  Payment        │
│  Service        │
└────┬────────────┘
     │ 7. Publish Event
     │    "PaymentCompleted"
     ▼
┌─────────────────┐
│   RabbitMQ      │
└────┬────────────┘
     │
     ├──────────────────────────┐
     │                          │
     ▼                          ▼
┌─────────────────┐      ┌─────────────────┐
│  Pharmacy       │      │  Notification   │
│  Service        │      │  Service        │
└────┬────────────┘      └────┬────────────┘
     │                        │
     │ 8. Fulfill Order       │ 8. Send Receipt
     ▼                        ▼
┌─────────────────┐      ┌─────────────────┐
│  Pharmacy DB    │      │   Patient       │
└─────────────────┘      └─────────────────┘
```

---

## 5. Scalability Pattern

### Horizontal Scaling

```
Normal Load:
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │
     ├──────────┬──────────┬──────────┐
     ▼          ▼          ▼          ▼
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│ User    │ │Appointmt│ │  EHR    │ │Pharmacy │
│Service  │ │Service  │ │Service  │ │Service  │
│ x1      │ │ x2      │ │ x1      │ │ x1      │
└─────────┘ └─────────┘ └─────────┘ └─────────┘


Peak Load (Auto-scaled):
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │
     ├──────────┬──────────┬──────────┐
     ▼          ▼          ▼          ▼
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│ User    │ │Appointmt│ │  EHR    │ │Pharmacy │
│Service  │ │Service  │ │Service  │ │Service  │
│ x3 ⬆️   │ │ x5 ⬆️⬆️ │ │ x2 ⬆️   │ │ x1      │
└─────────┘ └─────────┘ └─────────┘ └─────────┘
```

---

## 6. Database per Service Pattern

```
┌──────────────────────────────────────────────────────────┐
│                    Application Layer                      │
│                                                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐│
│  │  User    │  │Appointmt │  │   EHR    │  │ Pharmacy ││
│  │ Service  │  │ Service  │  │ Service  │  │ Service  ││
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘│
└───────┼─────────────┼─────────────┼─────────────┼───────┘
        │             │             │             │
        │ Own DB      │ Own DB      │ Own DB      │ Own DB
        ▼             ▼             ▼             ▼
┌───────────┐  ┌───────────┐  ┌───────────┐  ┌───────────┐
│  User DB  │  │  Appt DB  │  │  EHR DB   │  │Pharmacy DB│
│           │  │           │  │           │  │           │
│ - users   │  │ - appts   │  │ - records │  │ - meds    │
│ - roles   │  │ - slots   │  │ - prescr  │  │ - orders  │
│           │  │ - schedule│  │ - labs    │  │ - stock   │
└───────────┘  └───────────┘  └───────────┘  └───────────┘

No Direct Database Access Between Services!
Communication via APIs and Events Only.
```

---

## 7. Security Architecture

```
┌─────────┐
│ Client  │
└────┬────┘
     │ 1. Login Request
     │    (username, password)
     ▼
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │ 2. Forward to Auth
     ▼
┌─────────────────┐
│  User Service   │
│  - Verify Creds │
│  - Generate JWT │
└────┬────────────┘
     │ 3. Return JWT Token
     ▼
┌─────────────────┐
│  API Gateway    │
└────┬────────────┘
     │ 4. Return to Client
     ▼
┌─────────┐
│ Client  │
│ (Store  │
│  Token) │
└────┬────┘
     │ 5. Subsequent Requests
     │    (with JWT in Header)
     ▼
┌─────────────────┐
│  API Gateway    │
│  - Validate JWT │
│  - Check Roles  │
│  - Rate Limit   │
└────┬────────────┘
     │ 6. Authorized Request
     ▼
┌─────────────────┐
│  Target Service │
└─────────────────┘
```

---

## 8. Monitoring & Observability

```
┌─────────────────────────────────────────────────────────┐
│                   Microservices                          │
│  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐     │
│  │User  │  │Appt  │  │ EHR  │  │Pharma│  │Paymt │     │
│  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘     │
└─────┼─────────┼─────────┼─────────┼─────────┼──────────┘
      │         │         │         │         │
      │ Logs    │ Logs    │ Logs    │ Logs    │ Logs
      ▼         ▼         ▼         ▼         ▼
┌──────────────────────────────────────────────────────────┐
│              Elasticsearch (Log Storage)                  │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
                  ┌──────────────┐
                  │   Kibana     │
                  │ (Log Search) │
                  └──────────────┘

      │ Metrics │ Metrics │ Metrics │ Metrics │ Metrics
      ▼         ▼         ▼         ▼         ▼
┌──────────────────────────────────────────────────────────┐
│              Prometheus (Metrics Storage)                 │
└────────────────────────┬─────────────────────────────────┘
                         │
                         ▼
                  ┌──────────────┐
                  │   Grafana    │
                  │ (Dashboard)  │
                  └──────────────┘

      │ Traces  │ Traces  │ Traces  │ Traces  │ Traces
      ▼         ▼         ▼         ▼         ▼
┌──────────────────────────────────────────────────────────┐
│              Zipkin (Distributed Tracing)                 │
│                                                            │
│  Request Flow:                                            │
│  Gateway → User → Appointment → EHR                       │
│   50ms     30ms      120ms       80ms                     │
│  Total: 280ms                                             │
└──────────────────────────────────────────────────────────┘
```

---

## 9. Deployment Architecture (Kubernetes)

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                        │
│                                                               │
│  ┌────────────────────────────────────────────────────────┐ │
│  │                    Ingress Controller                   │ │
│  │              (External Load Balancer)                   │ │
│  └──────────────────────┬─────────────────────────────────┘ │
│                         │                                    │
│  ┌──────────────────────▼─────────────────────────────────┐ │
│  │                  API Gateway Service                    │ │
│  │                   (3 Replicas)                          │ │
│  └──────────────────────┬─────────────────────────────────┘ │
│                         │                                    │
│  ┌──────────────────────┴─────────────────────────────────┐ │
│  │                                                          │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │ │
│  │  │User Service │  │Appt Service │  │ EHR Service │   │ │
│  │  │(3 replicas) │  │(5 replicas) │  │(2 replicas) │   │ │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘   │ │
│  │         │                │                │            │ │
│  │  ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐   │ │
│  │  │User DB Pod  │  │Appt DB Pod  │  │EHR DB Pod   │   │ │
│  │  │(StatefulSet)│  │(StatefulSet)│  │(StatefulSet)│   │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │ │
│  │                                                          │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │ │
│  │  │Pharmacy Svc │  │Payment Svc  │  │Notif Service│   │ │
│  │  │(2 replicas) │  │(3 replicas) │  │(3 replicas) │   │ │
│  │  └─────────────┘  └─────────────┘  └─────────────┘   │ │
│  │                                                          │ │
│  └──────────────────────────────────────────────────────────┘ │
│                                                               │
│  ┌──────────────────────────────────────────────────────────┐ │
│  │              Infrastructure Services                      │ │
│  │                                                            │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐│ │
│  │  │ RabbitMQ │  │  Redis   │  │ Service  │  │  Config  ││ │
│  │  │   Pod    │  │   Pod    │  │ Registry │  │  Server  ││ │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘│ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 10. Failure Handling - Circuit Breaker Pattern

```
Normal Operation:
┌─────────────┐         ┌─────────────┐
│ Appointment │────────►│    EHR      │
│  Service    │  ✅ OK  │  Service    │
└─────────────┘         └─────────────┘


EHR Service Failing:
┌─────────────┐         ┌─────────────┐
│ Appointment │────────►│    EHR      │
│  Service    │  ❌ Fail│  Service    │
└─────────────┘         └──────X──────┘
      │                       (Down)
      │ After 5 failures
      ▼
┌─────────────┐
│Circuit Open │
│ (Stop calls)│
└─────────────┘
      │
      │ Return fallback response
      ▼
┌─────────────┐
│   Client    │
│ (Degraded   │
│  Service)   │
└─────────────┘


Recovery:
┌─────────────┐         ┌─────────────┐
│ Appointment │────────►│    EHR      │
│  Service    │  Test   │  Service    │
└─────────────┘         └─────────────┘
      │                       │
      │ If success            │ ✅ Recovered
      ▼                       ▼
┌─────────────┐         ┌─────────────┐
│Circuit Close│         │   Normal    │
│(Resume calls│         │  Operation  │
└─────────────┘         └─────────────┘
```

---

## Catatan Diagram

Diagram-diagram di atas mengilustrasikan:

1. **High-Level Architecture**: Overview keseluruhan sistem
2. **Service Communication**: Pola komunikasi synchronous dan asynchronous
3. **Business Flows**: End-to-end flow untuk use cases spesifik
4. **Scalability**: Horizontal scaling pattern
5. **Data Isolation**: Database per service pattern
6. **Security**: Authentication dan authorization flow
7. **Observability**: Monitoring dan logging architecture
8. **Deployment**: Kubernetes deployment strategy
9. **Resilience**: Circuit breaker untuk fault tolerance

Diagram-diagram ini dapat digunakan untuk:
- Dokumentasi teknis
- Onboarding developer baru
- Presentasi ke stakeholders
- Architecture review
- Troubleshooting dan debugging
