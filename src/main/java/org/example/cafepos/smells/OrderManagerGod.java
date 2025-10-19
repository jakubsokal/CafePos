package org.example.cafepos.smells;

import org.example.cafepos.common.Money;
import org.example.cafepos.common.Product;
import org.example.cafepos.factory.ProductFactory;

/*The first smell is a God Class because the OrderManagerGod class handles multiple responsibilities,
 including order processing, payment handling, and receipt printing. This violates the Single Responsibility Principle,
 making the class difficult to maintain and extend. */
public class OrderManagerGod {
    public static int TAX_PERCENT = 10;

    public static String LAST_DISCOUNT_CODE = null;

    public static String process
            (String recipe, int qty, String paymentType,
             String discountCode, boolean printReceipt) {
        ProductFactory factory = new ProductFactory();

        //Creating Product
        Product product = factory.create(recipe);
        Money unitPrice;

        //Pricing Product
        try {
            var priced = product instanceof  org.example.cafepos.common.Priced
                    p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        //Calculates the discount value based on the discount code
        if (qty <= 0) qty = 1;
        Money subtotal = unitPrice.multiply(qty);
        Money discount = Money.zero();

        if (discountCode != null) {
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            LAST_DISCOUNT_CODE = discountCode;
        }

        //Calculating subtotal with discount applied, tax, and total
        Money discounted =
                Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));

        if (discounted.asBigDecimal().signum() < 0) discounted =
                Money.zero();

        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100)));

        var total = discounted.add(tax);

        //Handling Payment I/O based on payment type
        if (paymentType != null) {
            if (paymentType.equalsIgnoreCase("CASH")) {
                System.out.println("[Cash] Customer paid " + total + "EUR");
            } else if (paymentType.equalsIgnoreCase("CARD")) {
                System.out.println("[Card] Customer paid " + total + " EUR with card ****1234");
            } else if (paymentType.equalsIgnoreCase("WALLET")) {
                System.out.println("[Wallet] Customer paid " + total + " EUR via wallet user-wallet-789");
            } else {
                System.out.println("[UnknownPayment] " + total);
            }
        }

        //Printing Receipt
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");

        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }

        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
                receipt.append("Total: ").append(total);

        String out = receipt.toString();

        if (printReceipt) {
            System.out.println(out);
        }

        return out;
    }
}
