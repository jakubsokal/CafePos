# Cafe POS System
## Overview
All of our unit test run and pass successfully. We covered pass cases, fail cases and edge cases for each test class.

# Decisions
Smells Removed -<br />
God Class - we split the responsibilities of OrderManagerGod into CheckoutService,PricingService,ReceiptPrinter,and PaymentStrategy<br />
Primitive Obsession - we replaces string based discount and payment logic into polymorphic strategies<br />
Global/Static State - we removed TAX_PERCENT and LAST_DISCOUNT_CODE and replaced them with injected values<br />
Duplicate Code -we extracted reusable logic from discount and tax calculations<br />
Shotgun Surgery - we put all pricing logic together to avoid scattered changes<br />

Refactoring -
Extract Class (DiscountPolicy, TaxPolicy, ReceiptPrinter)<br />
Replace Conditional with Polymorphism (PaymentStrategy)<br />
Constructor Injection for all dependencies<br />
Remove Global State by eliminating static fields<br />

SOLID principles satisfied<br />
Single Responsibility — each class has one clear purpose <br />
Open/Closed — new discount policies, tax policies, payment types can be added without modifying existing code<br />
Liskov Substitution — subclasses of PaymentStrategy, DiscountPolicy and TaxPolicy can be used interchangeably<br />
Dependency Inversion — high-level modules depend on abstractions, not concrete classes

# Flow
CheckoutService --> ProductFactory --> PricingService --> PaymentStrategy --> ReceiptPrinter

# Responsibilities:
CheckoutService now handles the checkout process by coordinating with PricingService, ReceiptPrinter, and PaymentStrategy.
It controls the overall flow without being any business logic inside it instead it delegates to other classes.

ProductFactory creates Product instances based on product codes and addons, encapsulating product creation logic.

PricingService handles all pricing related logic, including applying discounts and calculating taxes.
It uses DiscountPolicy and TaxPolicy to encapsulate specific strategies for discounts and tax calculations.

ReceiptPrinter is responsible for formatting and printing the receipt.

PaymentStrategy handles payment processing based on the selected payment method and delegates to a specific payment.


# WEEK 8 <br/>
Where does Command decouple UI from business logic in your codebase? <br/>
•	In the command classes (AddItemCommand and MacroCommand) o	Business operations encapsulated, such as adding items. They are created with data and dependencies without knowledge of the UI or when they’ll be called <br/>
<br/>•	Also, in the invoker (PosRemote) o	When a button is pressed, the remote just calls the execute() function but doesn’t know anything about the logic its performing
<br/>
<br/>
Why is adapting the legacy printer better than changing your domain or vendor class? <br/>
It is better to adapt the legacy printer because changing the domain or vendor class could introduce more complexity and potential bugs into the system. Adapting the legacy printer allows us to create a bridge between our current system and the legacy code, ensuring that we can continue to use existing functionality without disrupting the overall architecture. This approach minimizes risk and maintains stability while still allowing for necessary updates and improvements.
It also ensures open/closed principles are followed as we are not modifying existing code but rather extending it.

# WEEK 9

**Where did you choose safety over transparency in your Composite API and why?**

I chose safety by placing add(), remove(), and getChild() in MenuComponent but 
throwing UnsupportedOperationException by default.  
Only Menu overrides these—MenuItem leaves them throwing exceptions.

**Why? ** Calling menuItem.add(... ) is meaningless for a leaf node. 
This design prevents logically invalid operations at the cost of potential runtime exceptions. 
Since menu construction happens in controlled initialization code (not scattered across the app),
this trade-off is acceptable. It's better to fail fast with a clear exception than allow operations 
that violate the domain model.

**What new behavior becomes easy with State that was awkward with conditionals?**

The State pattern made three things trivially easy:

1. **Adding new states:** Creating CancelledState requires one new class, zero changes to existing 
states (Open/Closed Principle). With conditionals, I'd modify every method.

2. **Understanding transitions:** PreparingState.markReady() shows exactly what's valid and where it leads. 
With conditionals, valid transitions were buried across scattered if (status == ...) checks.

3. **Testing:** I can test ReadyState.deliver() directly without executing the full 
New → Preparing → Ready flow.

The pattern transformed tangled conditional logic into a clean FSM where each state is a self-contained,
testable unit.

# Week 10<br/>
## Architecture: Layered Monolith

### Why Layered Monolith?
We picked a layered monolith architecture to keep it simple during early development while the domain model 
stabilizes. This provides clear separation of concerns through well defined layers 
(presentation, application, domain, infrastructure) without the operational complexity 
of distributed systems. It allows rapid iteration, simplified debugging, and easier testing 
while establishing clean architectural boundaries that will facilitate future decomposition.

### Natural Seams for Future Partitioning
**Payments**: The `PaymentStrategy` abstraction (with `CardPayment`, `CashPayment`, `WalletPayment`) 
is already decoupled and could become an independent Payment Service. Each payment type has distinct 
processing requirements and external integrations.

**Notifications**: The Observer pattern implementation (`CustomerNotifier`, `KitchenDisplay`, `DeliveryDesk`) 
forms a natural Notification Service boundary. These components react to order events without 
affecting core business logic.

**Pricing/Discounts**: `PricingService` with its `DiscountPolicy` and `TaxPolicy` strategies 
could evolve into a Promotion Service, especially as promotional rules grow more complex.

### Connectors/Protocols for Splitting
- **Events**: Async message queue (RabbitMQ/Kafka) for order lifecycle events (OrderCreated, OrderPaid, OrderReady)
- **REST APIs**: Synchronous HTTP for payment processing, pricing calculations
- **Webhooks**: For external payment gateway callbacks
- **gRPC**: For low-latency inter-service calls between core services

# ADR 1: Composite Pattern for Menu
## Context/Problem
The cafe menu has a hierarchical structure: main categories (Drinks, Food) contain subcategories
(Hot Drinks, Cold Drinks) which contain individual items (Espresso, Latte).
We needed a uniform way to treat both containers (menus) and items without complex type-checking code.

## Decision
Implemented Composite Pattern with:

MenuComponent abstract base class defining common interface
Menu (composite) can contain other MenuComponent objects
MenuItem (leaf) represents individual products
Both implement print() uniformly for recursive display
Code location: org.example.cafepos.menu package - MenuComponent. java, Menu.java, MenuItem.java

## Alternatives Considered
Separate Menu and Item classes with no common interface:
would require instanceof checks and casting everywhere
Single flat list of items with category strings: doesn't support 
nested categories, hard to navigate
Map<Category, List<Item>>: rigid structure, can't handle subcategories

## Consequences
**Pro:** Easy to add new menu levels (subcategories) without changing existing code<br />
**Pro:** Simplifies client code no need to check if something is a menu or item<br />
**Con:** Safety vs. transparency trade off add() throws UnsupportedOperationException on MenuItem(see Week 9 reflection)<br />
**Con:** Type safety reduced all nodes are MenuComponent, need runtime checks for specific operations<br />
**In the code:** Menu.print() recursively calls print() on all children. 
Clients treat entire menu tree uniformly without knowing the structure.

# ADR 2: Iterator Pattern for Menu
## Context/Problem
Needed to traverse the composite menu structure to filter items 
(e. g., find all vegetarian items) without exposing the internal List<MenuComponent> structure. 
Direct access to the children list would break encapsulation and make refactoring difficult.

## Decision
Implemented Iterator Pattern integrated with Composite:

Menu provides iterator() method returning Iterator<MenuComponent>
CompositeIterator handles recursive traversal of nested menus
vegetarianItems() uses the iterator to filter without exposing internals<br />
**Code location:** Menu.java - methods iterator(), vegetarianItems(), and CompositeIterator class

## Alternatives Considered
Expose getChildren() returning List: breaks encapsulation, clients depend on internal structure
Stream API only (no Iterator): Iterator is standard Java collection protocol, 
enables interoperability

## Consequences
**Pro:** Encapsulation maintained—internal structure hidden from clients<br />
**Pro:** Uniform traversal—same iterator works for simple and nested menus<br />
**Pro:** Supports filtering operations (vegetarianItems()) without exposing implementation<br />
**Pro:** Standard Java interface—works with for-each loops and stream operations<br />
**Con:** More complex than direct list access requires understanding of recursive iteration<br />
**In the code:** See Menu. vegetarianItems() which uses iterator() to traverse the
entire tree and filter MenuItem instances where vegetarian() == true.

# ADR 3: Adapter Pattern for Legacy Printer

## Context/Problem
Our system uses a modern Printer interface expecting String receipts, 
but we need to integrate with an existing LegacyThermalPrinter that only accepts byte[] data. 
We cannot modify the legacy printer (vendor code) or change our domain model to work with bytes.

## Decision
Implemented Adapter Pattern:

Created LegacyPrinterAdapter implementing our Printer interface
Adapter wraps LegacyThermalPrinter (adaptee)
Converts String to byte[] internally: receipt.getBytes(StandardCharsets.UTF_8)<br />
**Code location:** Infrastructure layer - LegacyPrinterAdapter. java adapts LegacyThermalPrinter. java

## Alternatives Considered
Modify domain to use byte[]: pollutes domain with infrastructure concerns
Modify vendor's LegacyThermalPrinter: we don't own the code, breaks on updates
Wrapper with different interface: doesn't integrate with our Printer abstraction

## Consequences
**Pro:** Domain remains clean works with String receipts as intended<br />
**Pro:** Legacy printer integrated without modification (Open/Closed Principle)<br />
**Pro:** Easy to swap printers just implement Printer interface<br />
**Pro:** Encapsulates conversion logic in one place<br />
**Con:** Extra class to maintain<br />
**Con:** Runtime conversion overhead (String to byte[]), though negligible for printing<br />
**In the code:** LegacyPrinterAdapter implements Printer. print(String) and internally 
calls legacyPrinter. printBytes(receipt.getBytes()). See Infrastructure layer for implementation.

# ADR 4: State Pattern for Order

## Context/Problem
Orders in the café system go through multiple states (New → Preparing → Ready → Delivered),
and each state has different valid operations. Initially, 
we used conditional logic (if (status == OrderStatus.NEW)) scattered across multiple methods in 
the Order class. This led to:

Tangled conditional logic making it hard to see valid state transitions
Shotgun Surgery when adding new states (modify every method)
Difficulty testing specific state behaviors in isolation
Risk of invalid transitions

## Decision
Implemented State Pattern with:

State interface defining all possible operations (pay(), prepare(), markReady(), deliver(), cancel())
Concrete state classes: NewState, PreparingState, ReadyState, DeliveredState
OrderFSM (context) delegates operations to current state
Each state encapsulates its own valid transitions and throws exceptions for invalid operations
Code location: org.example.cafepos.state package - see State. java, OrderFSM. java, and state implementations

## Alternatives Considered
Keep conditionals in Order class: scattered logic, violates Open/Closed, hard to test
Enum with switch statements: still requires modifying switch for new states, doesn't encapsulate behavior

## Consequences
**Pro:** Adding new states (e.g., CancelledState) requires one new class,
zero changes to existing states (Open/Closed Principle)<br />
**Pro:** Valid transitions PreparingState.markReady() clearly shows transition to ReadyState<br />
**Pro:** State specific behavior encapsulated each state class is small, focused, testable<br />
**Pro:** Testing simplified—can test ReadyState. deliver() directly without
executing full New → Preparing → Ready flow<br />
**Con:** More classes to manage<br />
**Con:** State transitions visible only at runtime, not in static structure<br />
**Con:** Each state must implement all operations<br />
**In the code:** See OrderFSM.pay() which delegates to currentState.pay(this). 
NewState.pay() transitions to PreparingState while other states throw IllegalStateException.
Each state knows only its valid next states, making the FSM explicit and maintainable.