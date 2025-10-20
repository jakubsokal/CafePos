Smells Removed -
God Class - we split the resposibilities of OrderManagerGod into CheckoutService,PricingService,ReceiptPrinter,and PaymentStrategy
Primitive Obsession - we replaces string based discount and payment logic into polymorphic strategies
Global/Static State - we removed TAX_PERCENT and LAST_DISCOUNT_CODE and replaced them with injected values
Duplicate Code -we extracted resuable logic from dicount and tax calculations
Shotgun Surgery - we put all pricing logic togther to avoid scattered changes

Refactoring -
Extract Class (DiscountPolicy, TaxPolicy, ReceiptPrinter)
Replace Conditional with Polymorphism (PaymentStrategy)
Constructor Injection for all dependencies
Remove Global State by eliminating static fields

SOLID principles satisfied-
Single Responsibility — each class has one clear purpose
Open/Closed — new discounts or payment types can be added without modifying existing code
Dependency Inversion — high-level modules depend on abstractions, not concrete classes

