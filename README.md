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