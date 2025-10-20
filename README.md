# Decisions
Smells Removed -
God Class - we split the responsibilities of OrderManagerGod into CheckoutService,PricingService,ReceiptPrinter,and PaymentStrategy
Primitive Obsession - we replaces string based discount and payment logic into polymorphic strategies
Global/Static State - we removed TAX_PERCENT and LAST_DISCOUNT_CODE and replaced them with injected values
Duplicate Code -we extracted reusable logic from discount and tax calculations
Shotgun Surgery - we put all pricing logic together to avoid scattered changes

Refactoring -
Extract Class (DiscountPolicy, TaxPolicy, ReceiptPrinter)
Replace Conditional with Polymorphism (PaymentStrategy)
Constructor Injection for all dependencies
Remove Global State by eliminating static fields

SOLID principles satisfied-
Single Responsibility — each class has one clear purpose
Open/Closed — new discounts or payment types can be added without modifying existing code
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
