package org.example.cafepos.demo;

import org.example.cafepos.printing.LegacyPrinterAdapter;
import org.example.cafepos.printing.Printer;
import vendor.legacy.LegacyThermalPrinter;

import java.util.Scanner;

public final class Week8Demo_Adapter {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter 'DEMO' for a predefined demo, anything else for normal run:");
        String input = in.nextLine();
        if (input.trim().equalsIgnoreCase("DEMO")) {
            DemoRun();
        } else {
            System.out.println("Please enter a receipt to print: ");
            String receipt = in.nextLine();
            InteractiveRun(receipt);
        }
    }

    private static void DemoRun(){
        String receipt = "Order (LAT+L) x2\nSubtotal: 7.80\nTax (10%): 0.78\nTotal: 8.58";
        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
        printer.print(receipt);
        System.out.println("[Demo] Sent receipt via adapter.");
    }

    private static void InteractiveRun(String receipt){
        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
        printer.print(receipt);
        System.out.println("[Demo] Sent receipt via adapter.");
    }
}
