## 1. Strategy Pattern

### 👉 Intent: Choose behavior (algorithm) at runtime.

### 🧠 When to use
You have multiple ways to perform a task </br>
You want to avoid big if-else / switch blocks </br>
You want interchangeable logic </br>
💡 Example: Payment strategies </br>

### 🧩 Key Idea

#### ➡️ Encapsulate behavior → swap anytime </br>


## 2. Factory Pattern

### 👉 Intent: Create objects without exposing creation logic

### 🧠 When to use
Object creation is complex </br>
You want to centralize creation </br>
You want loose coupling </br>
💡 Example: Notification factory </br>

### 🧩 Key Idea
#### ➡️ Delegate object creation → hide new
#### ➡️ Publisher notifies all subscribers automatically


## 3. Observer Pattern

### 👉 Intent: One-to-many dependency (event-based system)

### 🧠 When to use
Event systems (Kafka-like, listeners) </br>
UI updates </br>
Pub-sub systems </br>
💡 Example: YouTube subscribers </br>


## 4. Builder Pattern

#### 👉 Intent: Construct complex objects step-by-step

### 🧠 When to use
Object has many fields </br>
Avoid telescoping constructors </br>
Need immutability </br>
💡 Example: User object </br>
### 🧩 Key Idea

### 🧩 Key Idea

#### ➡️ Step-by-step object creation + readable code

