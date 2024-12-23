import java.util.List; 
// Importing the List interface for managing a collection of BankAccount objects.

import java.util.Map; 
// Importing the Map interface for storing account data where each account is accessed by its ID.

import java.util.concurrent.ConcurrentHashMap; 
// Importing the ConcurrentHashMap class for thread-safe storage of BankAccount objects.

import java.util.concurrent.CountDownLatch; 
// Importing the CountDownLatch class for synchronizing threads during transaction execution.

public class TransactionSystem { 
    // Defining the TransactionSystem class, which manages transfers between bank accounts.

    private final Map<Integer, BankAccount> accounts; 
    // Declaring a Map to store the BankAccount objects, indexed by their account IDs.

    private final CountDownLatch transaction1Latch = new CountDownLatch(1); 
    // Declaring a CountDownLatch to synchronize the execution of threads during the first transaction.

    private final CountDownLatch transaction2Latch = new CountDownLatch(1); 
    // Declaring a CountDownLatch to synchronize the execution of threads during the second transaction.

    public TransactionSystem(List<BankAccount> accountList) { 
        // Constructor to initialize the TransactionSystem with a list of BankAccount objects.

        this.accounts = new ConcurrentHashMap<>(); 
        // Initializing the accounts Map as a thread-safe ConcurrentHashMap.

        for (BankAccount account : accountList) { 
            // Iterating through the list of BankAccount objects.

            accounts.put(account.getId(), account); 
            // Storing each BankAccount in the accounts Map with its ID as the key.
        }
    }

    private void acquireLocksInOrder(BankAccount first, BankAccount second) { 
        // Private method to acquire locks on two BankAccount objects in a specific order to avoid deadlocks.

        if (first.getId() < second.getId()) { 
            // If the first account's ID is smaller, lock the first account first, then the second account.
            first.lock();
            second.lock();
        } else { 
            // Otherwise, lock the second account first, then the first account.
            second.lock();
            first.lock();
        }
    }

    private void releaseLocksInOrder(BankAccount first, BankAccount second) { 
        // Private method to release locks on two BankAccount objects in the same order as they were acquired.

        first.unlock(); 
        // Unlock the first account.

        second.unlock(); 
        // Unlock the second account.
    }

    public boolean transfer(int fromAccountId, int toAccountId, double amount, int threadNumber) { 
        // Method to perform a transfer from one account to another.

        try { 
            // Start of the try block.

            if (threadNumber == 2) { 
                // If thread number is 2, wait for the first transaction to finish before proceeding.

                transaction1Latch.await(); 
                // Wait for the latch to be counted down by the first transaction.
            } else if (threadNumber == 3) { 
                // If thread number is 3, wait for both transaction 1 and transaction 2 to finish before proceeding.

                transaction1Latch.await(); 
                // Wait for the first transaction to finish.

                transaction2Latch.await(); 
                // Wait for the second transaction to finish.
            }
        } catch (InterruptedException e) { 
            // Catching InterruptedException if the thread is interrupted while waiting.

            Thread.currentThread().interrupt(); 
            // Re-interrupt the current thread.

            return false; 
            // Return false if the thread was interrupted.
        }

        BankAccount fromAccount = accounts.get(fromAccountId); 
        // Retrieve the source BankAccount based on the account ID.

        BankAccount toAccount = accounts.get(toAccountId); 
        // Retrieve the destination BankAccount based on the account ID.

        if (fromAccount == null || toAccount == null) { 
            // Check if either the source or destination account is null.

            return false; 
            // Return false if either account is invalid (null).
        }

        System.out.println("Thread " + threadNumber + " attempting transfer of " + 
                          AccountUtils.formatCurrency(amount) + " from Account " + 
                          fromAccountId + " to Account " + toAccountId); 
        // Print message indicating the transfer attempt.

        acquireLocksInOrder(fromAccount, toAccount); 
        // Acquire locks on both accounts in a deadlock-free order.

        try { 
            // Start of the try block.

            if (fromAccount.getBalance() < amount) { 
                // Check if the source account has enough balance for the transfer.

                System.out.println("Transfer failed: Insufficient funds in Account " + fromAccountId); 
                // Print error message if funds are insufficient.

                return false; 
                // Return false if there are insufficient funds.
            }

            if (!fromAccount.withdraw(amount)) { 
                // Attempt to withdraw the specified amount from the source account.

                return false; 
                // Return false if the withdrawal fails.
            }

            toAccount.deposit(amount); 
            // Deposit the amount into the destination account.

            System.out.println("Thread " + threadNumber + " completed transfer successfully"); 
            // Print message indicating the transfer was successful.

            if (threadNumber == 1) { 
                // If this is thread 1, count down the latch for transaction 1.

                transaction1Latch.countDown(); 
            } else if (threadNumber == 2) { 
                // If this is thread 2, count down the latch for transaction 2.

                transaction2Latch.countDown(); 
            }

            return true; 
            // Return true indicating the transfer was successful.
        } catch (Exception e) { 
            // Catching any exception that occurs during the transfer.

            System.out.println("Error during transfer. Reversing transaction..."); 
            // Print message indicating that an error occurred, and the transaction is being reversed.

            reverseTransaction(fromAccountId, toAccountId, amount); 
            // Reverse the transaction to maintain consistency.

            return false; 
            // Return false if an error occurs during the transfer.
        } finally { 
            // Finally block to ensure locks are released.

            releaseLocksInOrder(fromAccount, toAccount); 
            // Release the locks on both accounts in the correct order.
        }
    }

    public void reverseTransaction(int fromAccountId, int toAccountId, double amount) { 
        // Method to reverse a transfer if an error occurs.

        BankAccount fromAccount = accounts.get(fromAccountId); 
        // Retrieve the source BankAccount.

        BankAccount toAccount = accounts.get(toAccountId); 
        // Retrieve the destination BankAccount.

        if (fromAccount == null || toAccount == null) { 
            // Check if either account is invalid.

            return; 
            // Return if either account is invalid (null).
        }

        acquireLocksInOrder(fromAccount, toAccount); 
        // Acquire locks on both accounts in the correct order.

        try { 
            // Start of the try block.

            fromAccount.deposit(amount); 
            // Deposit the amount back into the source account.

            toAccount.withdraw(amount); 
            // Withdraw the amount from the destination account.

            System.out.println("Transaction reversed successfully"); 
            // Print message indicating the reversal was successful.
        } finally { 
            // Finally block to ensure locks are released.

            releaseLocksInOrder(fromAccount, toAccount); 
            // Release the locks on both accounts.
        }
    }

    public void printAccountBalances() { 
        // Method to print the current balances of all accounts.

        System.out.println("\nCurrent Account Balances:"); 
        // Print the header for account balances.

        for (BankAccount account : accounts.values()) { 
            // Iterate through all the accounts in the Map.

            System.out.println("Account " + account.getId() + ": " + 
                             AccountUtils.formatCurrency(account.getBalance())); 
            // Print the ID and balance of each account.
        }
    }
}
