# TraceFlex

**TraceFlex** is a lightweight, extensible Java Agent for instrumenting methods in target applications to gather runtime execution metrics such as:
- Invocation count
- Execution time (min, max, average)
- Throughput

Collected stats are logged periodically and can be extended to be stored in databases like Couchbase, RocksDB, or others.

---

## 🚀 Features

- Bytecode instrumentation using **Javassist**
- Configurable method-level tracing via `application_xx.properties`
- Tracks:
    - Invocation count
    - Execution time (min/max/total)
    - Throughput (invocations/sec)
- Pluggable stats collector (currently `StatsHelper`, extendable)

---


## ⚙️ Setup & Usage

### 1. Configure Instrumentation Targets

Update `src/main/resources/application_xx.properties`:

```properties
instrumentedClasses=com.example.MyService
statInterval=5