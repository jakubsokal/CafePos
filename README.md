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
Why is adapting the legacy printer better than changing your domain or vendor class? <br/>
It is better to adapt the legacy printer because changing the domain or vendor class could introduce more complexity and potential bugs into the system. Adapting the legacy printer allows us to create a bridge between our current system and the legacy code, ensuring that we can continue to use existing functionality without disrupting the overall architecture. This approach minimizes risk and maintains stability while still allowing for necessary updates and improvements.
It also ensures open/closed principles are followed as we are not modifying existing code but rather extending it.