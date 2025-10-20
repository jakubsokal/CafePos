package org.example.cafepos.demo;

import org.example.cafepos.checkout.PricingService;
import org.example.cafepos.checkout.ReceiptPrinter;
import org.example.cafepos.checkout.discount.DiscountPolicy;
import org.example.cafepos.checkout.discount.FixedCouponDiscount;
import org.example.cafepos.checkout.discount.LoyaltyPercentDiscount;
import org.example.cafepos.checkout.discount.NoDiscount;
import org.example.cafepos.checkout.service.CheckoutService;
import org.example.cafepos.checkout.tax.FixedRateTaxPolicy;
import org.example.cafepos.common.Money;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.CardPayment;
import org.example.cafepos.payment.CashPayment;
import org.example.cafepos.payment.PaymentStrategy;
import org.example.cafepos.payment.WalletPayment;
import org.example.cafepos.smells.OrderManagerGod;

import java.util.Scanner;

public class Week6DemoInteractive {
    public static void main(String[] args) {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.print("Please enter a recipe for your order: ");
            String recipe = in.nextLine().toUpperCase();
            if(recipe.equals("EXIT")) break;
            System.out.print("Please enter quantity: ");
            int qty = Integer.parseInt(in.nextLine());
            System.out.print("Please enter payment type (CARD/CASH/WALLET): ");
            String paymentType = in.nextLine().toUpperCase();
            System.out.print("please enter loyalty code (LOYAL5/COUPON1/NONE): ");
            String loyaltyCode = in.nextLine().toUpperCase();

            String oldReceipt = OrderManagerGod.process(recipe, qty, paymentType,
                    loyaltyCode, false);

            DiscountPolicy discountPolicy;
            if (loyaltyCode.equals("LOYAL5")) {
                discountPolicy = new LoyaltyPercentDiscount(5);
            } else if (loyaltyCode.equals("COUPON1")) {
                discountPolicy = new FixedCouponDiscount(Money.of(1.00));
            } else {
                discountPolicy = new NoDiscount();
            }

            PaymentStrategy paymentStrategy;

            switch (paymentType) {
                case "CARD":
                    paymentStrategy = new CardPayment("1233456788652");
                    break;
                case "CASH":
                    paymentStrategy = new CashPayment();
                    break;
                case "WALLET":
                    paymentStrategy = new WalletPayment("WALLET-USER-001");
                    break;
                default:
                    paymentStrategy = new CashPayment();
                    System.out.println("Unknown payment type. Defaulting to CASH.");
            }
            var pricing = new PricingService(discountPolicy,
                    new FixedRateTaxPolicy(10));
            var printer = new ReceiptPrinter();
            var checkout = new CheckoutService(new ProductFactory(), pricing,
                    printer, 10);

            String newReceipt = checkout.checkout(recipe, qty, paymentStrategy);
            System.out.println("Old Receipt:\n" + oldReceipt);
            System.out.println("\nNew Receipt:\n" + newReceipt);
            System.out.println("\nMatch: " + oldReceipt.equals(newReceipt));
        }
    }
}
