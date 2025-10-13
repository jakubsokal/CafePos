package org.example.cafepos.demo;

import org.example.cafepos.common.Product;
import org.example.cafepos.domain.LineItem;
import org.example.cafepos.domain.Order;
import org.example.cafepos.domain.OrderIds;
import org.example.cafepos.domain.observers.CustomerNotifier;
import org.example.cafepos.domain.observers.DeliveryDesk;
import org.example.cafepos.domain.observers.KitchenDisplay;
import org.example.cafepos.factory.ProductFactory;
import org.example.cafepos.payment.CardPayment;
import org.example.cafepos.payment.CashPayment;
import org.example.cafepos.payment.WalletPayment;

import java.util.Scanner;

public final class Week5Demo {
    private static final String ERROR_COLOUR = "\u001B[31m";
    private static final String RESET_COLOUR = "\u001B[0m";
    private static Order CURRENT_ORDER;
    private static Scanner INPUT;
    private static final ProductFactory FACTORY = new ProductFactory();
    private static final KitchenDisplay KITCHEN = new KitchenDisplay();
    private static final DeliveryDesk DELIVERY = new DeliveryDesk();
    private static final CustomerNotifier CUSTOMER = new CustomerNotifier();

    public static void main(String[] args) {
        INPUT = new Scanner(System.in);
        CURRENT_ORDER = new Order(OrderIds.next());
        boolean running = true;

        while(running){
            System.out.print("""
               Welcome to the Cafe POS
               1. Add Item
               2. Pay
               3. See current order
               4. Exit
               Choose an option 1-5:\s""");

            String choice = INPUT.nextLine().trim();

            switch (choice) {
                case "1"-> addItemToOrder();
                case "2" -> payForOrder();
                case "3" -> seeCurrentOrder(CURRENT_ORDER);
                case "4" -> {
                    running = false;
                    try {
                        CURRENT_ORDER.unregister(KITCHEN);
                        CURRENT_ORDER.unregister(DELIVERY);
                        CURRENT_ORDER.unregister(CUSTOMER);
                    }catch (IllegalArgumentException ignored){
                        //If an order didn't register any observers we ignore the exception
                    }

                    System.out.print("Exiting Cafe POS. Goodbye!");
                }
                default -> System.out.println(ERROR_COLOUR + "Invalid choice. Please try again." + RESET_COLOUR);
            }

        }
    }

    private static void addItemToOrder(){
        CURRENT_ORDER.register(CUSTOMER);
        CURRENT_ORDER.register(KITCHEN);
        StringBuilder productId;
        while(true) {
            System.out.print("""
                    Here is our menu:\s
                    Product ID - Name (Price)
                    ----------------------------------------
                    ESP - Espresso ($2.50)
                    LAT - Latte Coffee ($4.00)
                    CAP - Cappuccino ($4.00)
                    ----------------------------------------
                    EXIT - Go back to main menu
                    Please enter the product ID of the item you want to add eg. ESP:\s""");

            productId = new StringBuilder(INPUT.nextLine().toUpperCase().trim());
            if (productId.toString().equals("EXIT")) return;

            if (!productId.toString().matches("ESP|LAT|CAP")) {
                System.out.println(ERROR_COLOUR + "Invalid product ID. Please try again." + RESET_COLOUR);
            }else break;
        }

        System.out.print("Would you like to add any customizations? (YES/NO): ");
        String customize = INPUT.nextLine().toUpperCase().trim();
        boolean addMOre = false;
        while(!addMOre) {
            if (customize.equals("YES")) {
                System.out.print("""
                        Available customizations:
                        ID - Customization (Additional Cost)
                        ----------------------------------------
                        OAT - Oat Milk ($0.50)
                        SHOT - Extra Shot ($0.80)
                        SYP - Syrup ($0.40)
                        L - Large Drink ($0.70)
                        ----------------------------------------
                        EXIT - Go back to main menu
                        Enter customization ID (e.g. OAT):\s""");
                String customizationChoice = INPUT.nextLine().toUpperCase().trim();
                if (customizationChoice.equals("EXIT")) return;

                switch (customizationChoice) {
                    case "OAT" -> productId.append("+OAT");
                    case "SHOT" -> productId.append("+SHOT");
                    case "SYP" -> productId.append("+SYP");
                    case "L" -> productId.append("+L");
                    default -> System.out.println(ERROR_COLOUR + "Invalid customization choice: " + customizationChoice + RESET_COLOUR);
                }

                System.out.print("Would you like to add more customizations? (YES/NO): ");
                customize = INPUT.nextLine().toUpperCase().trim();
                if (customize.equalsIgnoreCase("NO")) addMOre = true;

            } else if (customize.equals("NO")) {
                addMOre = true;
            }else {
                System.out.println(ERROR_COLOUR + "Invalid choice. Please try again." + RESET_COLOUR);
                System.out.print("Would you like to add any customizations? (YES/NO): ");
                customize = INPUT.nextLine().toUpperCase().trim();
            }
        }

        Product product = FACTORY.create(productId.toString());
        String quantityInput;
        while(true) {
            System.out.print("Enter quantity: ");
            quantityInput = INPUT.nextLine().trim();
            if (!quantityInput.matches("\\d+")) {
                System.out.println(ERROR_COLOUR + "Invalid quantity. Please enter a positive integer." + RESET_COLOUR);
            }else break;
        }
        int quantity = Integer.parseInt(quantityInput);
        CURRENT_ORDER.addItem(new LineItem(product, quantity));

    }

    private static void payForOrder(){
        if (!CURRENT_ORDER.items().isEmpty()) {
            CURRENT_ORDER.register(DELIVERY);
            boolean paid = false;
            while(!paid) {
                System.out.print("""
                        These are the available payment methods
                        ---------------------------------
                        CASH
                        CARD
                        WALLET
                        --------------------------------
                        EXIT - Go back to main menu
                        Select a payment method eg. CARD:\s""");
                String paymentChoice = INPUT.nextLine().toUpperCase().trim();
                switch (paymentChoice) {
                    case "CASH" -> {
                        CURRENT_ORDER.pay(new CashPayment());
                        paid = true;
                    }
                    case "CARD" -> {
                        CardPayment cardPayment;
                        while (true) {
                            System.out.print("Enter card number: ");
                            String cardNumber = INPUT.nextLine().trim();
                            try {
                                cardPayment = new CardPayment(cardNumber);
                                CURRENT_ORDER.pay(cardPayment);
                                paid = true;
                                break;
                            } catch (IllegalArgumentException e) {
                                System.out.println(ERROR_COLOUR + e.getMessage());
                                System.out.println("Please try again." + RESET_COLOUR);
                            }
                        }
                    }
                    case "WALLET" -> {
                        System.out.print("Enter wallet ID: ");
                        String walletId = INPUT.nextLine().trim();
                        CURRENT_ORDER.pay(new WalletPayment(walletId));
                        paid = true;
                    }
                    case "EXIT" -> {
                        return;
                    }
                    default -> System.out.println(ERROR_COLOUR + "Invalid payment method. Please try again." + RESET_COLOUR);
                }
            }
            CURRENT_ORDER.markReady();
            CURRENT_ORDER = new Order(OrderIds.next());
            CURRENT_ORDER.unregister(KITCHEN);
            CURRENT_ORDER.unregister(DELIVERY);
            CURRENT_ORDER.unregister(CUSTOMER);
            System.out.println("Starting a new order...");
        }else System.out.println("Order is empty. Please add items before paying.");
    }

    private static void seeCurrentOrder(Order currentOrder) {
        if (currentOrder.items().isEmpty()) {
            System.out.println("Order is empty. Please add items to the order.");
        } else {
            System.out.println("Current Order:");
            System.out.println("-------------------------------");
            for (LineItem item : currentOrder.items()) {
                System.out.println(item.quantity() + " x " + item.product().name() + " - " + item.lineTotal());
            }
            System.out.println("-------------------------------");
            System.out.println("Subtotal: " + currentOrder.subtotal());
            System.out.println("Total with tax (10%): " + currentOrder.totalWithTax(10));
            System.out.println("-------------------------------");
        }
    }
}