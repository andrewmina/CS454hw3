import java.util.List;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) throws InterruptedException {


        savingsAccount account = new savingsAccount(0);

        // Test depositing and withdrawing
        account.deposit(100);
        System.out.println("Balance after depositing 100: " + account.getBalance()); // should print 100



        // Test preferred withdrawals
        Thread t1 = new Thread(() -> {
            try {
                account.preferredWithdraw(40);
                System.out.println("Preferred withdrawal completed on thread 1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                account.ordinaryWithdraw(30);
                System.out.println("Ordinary withdrawal completed on thread 2");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });




        Thread t3 = new Thread(() -> {
            try {
                account.preferredWithdraw(20);
                System.out.println("Preferred withdrawal completed on thread 3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to complete
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final balance: " + account.getBalance()); // should print 10


        // Question 3:
        // No, there is a scenario where a race condition can occur when two threads
        // attempt to transfer between each other simultaneously  and the
        // lock trying to deposit is being blocked by the lock trying withdraw
        // a race condition occurs

    }
}