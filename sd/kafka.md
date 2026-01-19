# Kafka

> Kafka is a distributed, durable, ordered-per-partition event log that supports high throughput, fault tolerance, and scalable consumers.

### Core Concepts

- What is Kafka? Why is it used?
- Difference between queue vs log
- What is a topic, partition, offset
- Why is Kafka pull-based and not push-based?
- Why is Kafka fast?

### Architecture
- Role of Broker, Producer, Consumer
- What is a consumer group?
- How does Kafka achieve fault tolerance?
- What is replication factor?

### Partitions & Ordering (Very Common)

- Why does Kafka need partitions?
- Is message ordering guaranteed?
- Ordering across partitions?
- How do you decide number of partitions?
- What happens if you increase partitions later?
- Can two consumers read the same partition?

### Producers (High Probability)

- How does a producer choose a partition?
- What are keyed vs non-keyed messages?
- What is acks=0 / 1 / all?
- What happens if leader broker fails?
- What is idempotent producer?
- How does Kafka prevent duplicate messages?

### Consumers (Very Common)

- How does consumer group rebalancing work?
- When does rebalance happen?
- What problems does rebalance cause?
- Difference between: enable.auto.commit=true vs false
- How do you manually commit offsets?
- What happens if consumer crashes before commit?

  Delivery Semantics (VERY IMPORTANT)

Almost always asked:

# Semantics	Meaning
- At-most-once	May lose messages
- At-least-once	No loss, duplicates possible
- Exactly-once	No loss, no duplicates

Follow-ups:
- Does Kafka support exactly-once?
- How is exactly-once implemented?
- What is transactional producer?

### Offset Management (Favorite Topic)

- Where are offsets stored?
- How does offset commit work?
- What happens if offsets are lost?
- Can offsets be reset?
- Difference between earliest / latest
> auto.offset.reset=earliest | latest

### Failure Scenarios (Interviewers Love This)

What happens if:
- Producer crashes?
- Consumer crashes?
- Broker crashes?
- Leader partition crashes?
- How does Kafka recover?
- What is ISR (In-Sync Replicas)?
- What happens if ISR shrinks?

### Kafka vs Others (Comparison Questions)

- Kafka vs RabbitMQ
- Kafka vs SQS
- Kafka vs Pulsar
- Why Kafka is better for event streaming
- When Kafka is NOT a good choice

### Scaling & Performance

- How do you scale Kafka consumers?
- How do you scale producers?
- How do you increase throughput?
- Compression types (gzip, snappy, lz4)
- Batch size & linger.ms
- Zero-copy send

### Schema & Data Compatibility

- What is Schema Registry?
- Why use Avro / Protobuf?
- Forward vs backward compatibility
- What happens if schema changes?

### Exactly-Once & Transactions (Senior Level)
-What is Kafka transaction?
- What is producer fencing?
- How does Kafka handle duplicates?
- Can consumers be exactly-once?

### Monitoring & Operations

- How do you monitor Kafka?
- Consumer lag – what is it?
- How to handle slow consumers?
- Disk usage and retention policies

### Interview-Winning Questions (You Ask Them)

- How do you handle schema evolution?
- What happens during consumer rebalancing?
- How do you handle retries without duplication?
- How do you handle backpressure?
