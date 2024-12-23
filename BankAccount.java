import java.util.concurrent.locks.ReentrantLock; 
// Importing the ReentrantLock class to allow for locking mechanisms with exclusive access.

import java.util.concurrent.locks.ReadWriteLock; 
// Importing the ReadWriteLock interface to enable efficient locking for reading and writing.

import java.util.concurrent.locks.ReentrantReadWriteLock; 
// Importing the ReentrantReadWriteLock class to provide a read-write lock mechanism.

public class BankAccount { 
    // Defining a class representing a bank account.

    private int id; 
    // Declaring an integer variable to store the account's ID.

    private double balance; 
    // Declaring a double variable to store the balance of the account.

    private final ReentrantLock lock; 
    // Declaring a ReentrantLock to ensure exclusive access to the bank account.

    private final ReadWriteLock readWriteLock; 
    // Declaring a ReadWriteLock to allow multiple threads to read the balance concurrently but only one to write to it at a time.

    public BankAccount(int id) { 
        // Constructor for BankAccount that initializes the account ID and balance.

        this.id = id; 
        // Setting the account ID from the constructor parameter.

        // Generate random initial balance between $1000 and $5000
        this.balance = AccountUtils.generateInitialBalance(1000, 5000); 
        // Initializing the balance by generating a random value between $1000 and $5000 using the AccountUtils class.

        this.lock = new ReentrantLock(true); 
        // Initializing the ReentrantLock with fairness set to true, meaning threads acquire the lock in the order they requested it.

        this.readWriteLock = new ReentrantReadWriteLock(true); 
        // Initializing the ReadWriteLock with fairness set to true, meaning the lock grants read and write locks in the order they are requested.
    }

    public int getId() { 
        // Getter method to return the account ID.

        return id; 
        // Returning the account ID.
    }

    public double getBalance() { 
        // Getter method to return the balance of the account.

        readWriteLock.readLock().lock(); 
        // Acquiring the read lock before reading the balance to allow concurrent reads but prevent writes during this time.

        try {
            System.out.println("Reading balance of Account " + id + ": " + 
                             AccountUtils.formatCurrency(balance)); 
            // Printing the balance of the account in a formatted currency string.

            return balance; 
            // Returning the current balance.
        } finally {
            readWriteLock.readLock().unlock(); 
            // Releasing the read lock after the balance is read.
        }
    }

    public boolean withdraw(double amount) { 
        // Method to withdraw a specified amount from the account.

        readWriteLock.writeLock().lock(); 
        // Acquiring the write lock before modifying the balance to ensure exclusive access.

        try {
            if (balance >= amount) { 
                // Checking if the balance is sufficient for the withdrawal.

                balance -= amount; 
                // Subtracting the withdrawal amount from the balance.

                System.out.println("Withdrawn " + AccountUtils.formatCurrency(amount) + 
                                 " from Account " + id + ". New balance: " + 
                                 AccountUtils.formatCurrency(balance)); 
                // Printing the withdrawal amount and the new balance.

                return true; 
                // Returning true to indicate a successful withdrawal.
            }
            return false; 
            // Returning false if the balance is insufficient for the withdrawal.
        } finally {
            readWriteLock.writeLock().unlock(); 
            // Releasing the write lock after the withdrawal operation.
        }
    }

    public void deposit(double amount) { 
        // Method to deposit a specified amount into the account.

        readWriteLock.writeLock().lock(); 
        // Acquiring the write lock before modifying the balance to ensure exclusive access.

        try {
            balance += amount; 
            // Adding the deposit amount to the balance.

            System.out.println("Deposited " + AccountUtils.formatCurrency(amount) + 
                             " to Account " + id + ". New balance: " + 
                             AccountUtils.formatCurrency(balance)); 
            // Printing the deposit amount and the new balance.
        } finally {
            readWriteLock.writeLock().unlock(); 
            // Releasing the write lock after the deposit operation.
        }
    }

    public void lock() { 
        // Method to explicitly acquire the ReentrantLock for the account.

        lock.lock(); 
        // Acquiring the lock to perform some critical operation with exclusive access.
    }

    public void unlock() { 
        // Method to explicitly release the ReentrantLock for the account.

        lock.unlock(); 
        // Releasing the lock after the critical operation is complete.
    }
}
