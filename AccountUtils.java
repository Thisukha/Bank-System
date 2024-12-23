import java.util.Random; 
// Importing the Random class to generate random numbers.

public class AccountUtils { 
    // Defining a utility class for account-related operations.

    private static final Random random = new Random(); 
    // Creating a single instance of Random to be used across the methods in the class.

    // Generate random initial balance between min and max value
    public static double generateInitialBalance(double minBalance, double maxBalance) { 
        // Method to generate a random initial balance within a specified range (minBalance to maxBalance).

        return Math.round((minBalance + (random.nextDouble() * (maxBalance - minBalance))) * 100.0) / 100.0; 
        // Calculate a random value within the range, round it to two decimal places, and return it.
    }

    // Generate random transfer amount between min and max value
    public static double generateTransferAmount(double minAmount, double maxAmount) { 
        // Method to generate a random transfer amount within a specified range (minAmount to maxAmount).

        return Math.round((minAmount + (random.nextDouble() * (maxAmount - minAmount))) * 100.0) / 100.0; 
        // Calculate a random value within the range, round it to two decimal places, and return it.
    }

    // Format currency for display
    public static String formatCurrency(double amount) { 
        // Method to format a double value as a currency string.

        return String.format("$%.2f", amount); 
        // Format the amount to two decimal places and prepend a dollar sign ($).
    }
} 
