package org.example.cafepos.payment;

import org.example.cafepos.domain.Order;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;

    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(Order order) {
        String maskedCard = maskCardNumber(cardNumber);

        System.out.println("[Card] Customer paid " +
                order.totalWithTax(10) + " EUR with card " + maskedCard);
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 16) {
            throw new IllegalArgumentException("Card number must be 16 digits long");
        }else if (!cardNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Card number must contain only digits");
        }

        return "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
