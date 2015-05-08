package it.thomasjohansen.hello.cli;

import static java.lang.System.console;

public class CLIUtils {

    public static String readInput(String prompt) {
        String input;
        do {
            input = console().readLine(prompt);
        } while (input.trim().length() == 0);
        return input;
    }

    public static String readOptionalInput(String prompt) {
        String input = console().readLine(prompt);
        if (input.trim().length() == 0)
            return null;
        return input.trim();
    }

    public static String readPassword(String prompt) {
        char[] input;
        do {
            input = console().readPassword(prompt);
        } while (input.length == 0);
        return new String(input);
    }

    public static long readInputLong(String prompt) {
        while (true) {
            String input = readInput(prompt);
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
            }
        }
    }

    public static int readInputInt(String prompt, int minValue, int maxValue) {
        while (true) {
            String input = readInput(prompt);
            try {
                int i = Integer.parseInt(input);
                if (i >= minValue && i <= maxValue)
                    return i;
            } catch (NumberFormatException e) {
            }
        }
    }

    public static boolean empty(String s) {
        return s.trim().length() == 0;
    }

}
