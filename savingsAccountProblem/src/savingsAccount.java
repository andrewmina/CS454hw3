import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class savingsAccount {
    private int balance;
    private int preferredWithdrawalQueue;
    private Lock lock;
    private Condition sufficientFunds;
    private Condition noPreferredWithdrawal;

    public savingsAccount(int initialBalance) {
        balance = initialBalance;
        preferredWithdrawalQueue = 0;
        lock = new ReentrantLock();
        sufficientFunds = lock.newCondition();
        noPreferredWithdrawal = lock.newCondition();
    }

    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    void transfer(int k, savingsAccount reserve) throws InterruptedException {
        lock.lock();
        try {
            reserve.ordinaryWithdraw(k);
            deposit(k);
        } finally {
            lock.unlock();
        }
    }


    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
            sufficientFunds.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void ordinaryWithdraw(int amount) throws InterruptedException {
        lock.lock();
        try {
            while (preferredWithdrawalQueue > 0 || balance < amount) {
                noPreferredWithdrawal.await();
            }
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    public void preferredWithdraw(int amount) throws InterruptedException {
        lock.lock();
        try {
            preferredWithdrawalQueue++;
            while (balance < amount) {
                sufficientFunds.await();
            }
            preferredWithdrawalQueue--;
            balance -= amount;
            noPreferredWithdrawal.signalAll();
        } finally {
            lock.unlock();
        }
    }
}