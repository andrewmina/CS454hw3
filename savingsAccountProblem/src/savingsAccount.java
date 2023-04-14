import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class savingsAccount {
    private final Lock lock = new ReentrantLock();
    private final Condition sufficientFunds = lock.newCondition();
    private final Condition sufficientFundsOrdinary = lock.newCondition();
    private final Semaphore semaphore = new Semaphore(1);
    private int balance;
    private int numPreferredWithdrawalsWaiting;

    public savingsAccount(int initialBalance) {
        this.balance = initialBalance;
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

    public void preferredWithdraw(int amount) throws InterruptedException {
        lock.lock();
        try {
            semaphore.acquire();
            numPreferredWithdrawalsWaiting++;
            while (balance < amount || numPreferredWithdrawalsWaiting > 1) {
                sufficientFundsOrdinary.await();
            }
            numPreferredWithdrawalsWaiting--;
            balance -= amount;
            sufficientFunds.signalAll();
        } finally {
            semaphore.release();
            lock.unlock();
        }
    }

    public void ordinaryWithdraw(int amount) throws InterruptedException {
        lock.lock();
        try {
            while (balance < amount || numPreferredWithdrawalsWaiting > 0) {
                sufficientFunds.await();
            }
            balance -= amount;
            sufficientFundsOrdinary.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public void transfer(int amount, savingsAccount reserve) {
        lock.lock();
        try {
            reserve.preferredWithdraw(amount);
            deposit(amount);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
