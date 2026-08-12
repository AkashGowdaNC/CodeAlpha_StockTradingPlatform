package com.codealpha.trading;

import java.util.Scanner;

/** Validated console input so bad typing never crashes the platform. */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("  [!] This field cannot be left blank.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            String raw = readLine(prompt);
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.println("  [!] Enter a whole number between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("  [!] '" + raw + "' is not a whole number.");
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (y/n): ").toLowerCase();
            if (value.equals("y") || value.equals("yes")) return true;
            if (value.equals("n") || value.equals("no"))  return false;
            System.out.println("  [!] Please answer with y or n.");
        }
    }

    public void pause() {
        System.out.print("\n  Press ENTER to continue...");
        scanner.nextLine();
    }
}
