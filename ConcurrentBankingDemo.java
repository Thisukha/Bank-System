import java.util.Arrays; 
// Importing Arrays class to work with arrays and list conversions.

import java.util.List; 
// Importing the List interface to store multiple BankAccount objects.

public class ConcurrentBankingDemo { 
    // Defining the main class for the concurrent banking simulation.

    public static void main(String[] args) { 
        // Main method to start the banking system simulation.

        // Initialize accounts with random balances
        List<BankAccount> accounts = Arrays.asList( 
            // Creating a list of BankAccount objects with randomly generated balances.
            new BankAccount(1), 
            // Creating a BankAccount with ID 1.
            new BankAccount(2), 
            // Creating a BankAccount with ID 2.
            new BankAccount(3) 
            // Creating a BankAccount with ID 3.
        );

        TransactionSystem system = new TransactionSystem(accounts); 
        // Initializing a TransactionSystem instance to manage the accounts and transactions.

        // Generate random transfer amounts
        double transfer1Amount = AccountUtils.generateTransferAmount(50, 200); 
        // Generating a random transfer amount between $50 and $200 for the first transaction.

        double transfer2Amount = AccountUtils.generateTransferAmount(100, 300); 
        // Generating a random transfer amount between $100 and $300 for the second transaction.

        double transfer3Amount = AccountUtils.generateTransferAmount(25, 150); 
        // Generating a random transfer amount between $25 and $150 for the third transaction.

        // Create threads with random transfer amounts
        Thread thread1 = new Thread(() -> { 
            // Creating the first thread that performs a transfer from account 1 to account 2.
            system.transfer(1, 2, transfer1Amount, 1); 
            // Performing the transfer from account 1 to account 2 with the generated amount.
        }, "Thread-1"); 
        // Naming the thread "Thread-1".

        Thread thread2 = new Thread(() -> { 
            // Creating the second thread that performs a transfer from account 2 to account 3.
            system.transfer(2, 3, transfer2Amount, 2); 
            // Performing the transfer from account 2 to account 3 with the generated amount.
        }, "Thread-2"); 
        // Naming the thread "Thread-2".

        Thread thread3 = new Thread(() -> { 
            // Creating the third thread that performs a transfer from account 3 to account 1.
            system.transfer(3, 1, transfer3Amount, 3); 
            // Performing the transfer from account 3 to account 1 with the generated amount.
        }, "Thread-3"); 
        // Naming the thread "Thread-3".

        Thread thread4 = new Thread(() -> { 
            // Creating the fourth thread that performs balance readings for accounts 1 and 3.
            System.out.println("\nThread 4 reading balances:"); 
            // Printing a message indicating that thread 4 is reading balances.

            accounts.get(0).getBalance(); 
            // Thread 4 reads the balance of account 1.

            accounts.get(2).getBalance(); 
            // Thread 4 reads the balance of account 3.
        }, "Thread-4"); 
        // Naming the thread "Thread-4".

        System.out.println("Initial account balances:"); 
        // Printing a message to indicate the start of the initial account balances display.

        system.printAccountBalances(); 
        // Calling the method to print the current balances of all the accounts.

        System.out.println("\nStarting transactions with random amounts...\n"); 
        // Printing a message indicating that the transactions are starting.

        // Start all threads
        thread1.start(); 
        // Starting thread 1 to perform the first transfer.

        thread2.start(); 
        // Starting thread 2 to perform the second transfer.

        thread3.start(); 
        // Starting thread 3 to perform the third transfer.

        thread4.start(); 
        // Starting thread 4 to perform the balance reading.

        // Wait for all threads to complete
        try { 
            // Starting the try block to wait for the completion of the threads.
            thread1.join(); 
            // Wait for thread 1 to finish.

            thread2.join(); 
            // Wait for thread 2 to finish.

            thread3.join(); 
            // Wait for thread 3 to finish.

            thread4.join(); 
            // Wait for thread 4 to finish.
        } catch (InterruptedException e) { 
            // Catching any InterruptedException that may occur during the join process.
            e.printStackTrace(); 
            // Printing the stack trace of the exception.
        }

        System.out.println("\nAll transactions completed."); 
        // Printing a message indicating that all transactions have completed.

        system.printAccountBalances(); 
        // Printing the final account balances after all transactions have been completed.
    }
}
