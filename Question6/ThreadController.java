package Question6;

import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class NumberPrinter {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int x) {
        System.out.print(x);
    }

    public void printOdd(int x) {
        System.out.print(x);
    }
}

class ThreadController {
    private int n;
    private int counter = 1; // Shared counter to maintain sequence
    private Lock lock = new ReentrantLock();
    private Condition zeroTurn = lock.newCondition();
    private Condition evenTurn = lock.newCondition();
    private Condition oddTurn = lock.newCondition();
    private boolean printZero = true;

    public ThreadController(int n) {
        this.n = n;
    }

    public void printZero(NumberPrinter printer) {
        for (int i = 1; i <= n; i++) {
            lock.lock();
            try {
                while (!printZero) {
                    zeroTurn.await();
                }
                printer.printZero();
                printZero = false;
                if (counter % 2 == 0) {
                    evenTurn.signal();
                } else {
                    oddTurn.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printEven(NumberPrinter printer) {
        for (int i = 2; i <= n; i += 2) {
            lock.lock();
            try {
                while (printZero || counter % 2 != 0) {
                    evenTurn.await();
                }
                printer.printEven(i);
                counter++;
                printZero = true;
                zeroTurn.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void printOdd(NumberPrinter printer) {
        for (int i = 1; i <= n; i += 2) {
            lock.lock();
            try {
                while (printZero || counter % 2 == 0) {
                    oddTurn.await();
                }
                printer.printOdd(i);
                counter++;
                printZero = true;
                zeroTurn.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter n: ");
        int n = scanner.nextInt();
        scanner.close();

        NumberPrinter printer = new NumberPrinter();
        ThreadController controller = new ThreadController(n);

        Thread zeroThread = new Thread(() -> controller.printZero(printer));
        Thread evenThread = new Thread(() -> controller.printEven(printer));
        Thread oddThread = new Thread(() -> controller.printOdd(printer));

        zeroThread.start();
        evenThread.start();
        oddThread.start();
    }
}

