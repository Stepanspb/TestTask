package Main;

import Element.Element;
import QueueProcessing.Manager;
import QueueProcessing.QueueLoader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Main {

    private static final int GROUPSQUANTITY = 15, // Колличество групп элементов
                             PROCESSQUANTITY = 11, // Количество процессов обработки
                             MAXELEMENT = 10000; // вместимость очереди
    private static final BlockingQueue<Element> QUEUE 
            = new ArrayBlockingQueue<>(MAXELEMENT); // Очередь элементов
    
    private static CountDownLatch threadStartLatch = new CountDownLatch(GROUPSQUANTITY);
    private static CountDownLatch QueueLoaderLatch = new CountDownLatch(GROUPSQUANTITY);
    
    
    public static void main(String[] args) throws InterruptedException {
         // Заполняем очередь        
        for (int i = 0; i < GROUPSQUANTITY; i++) {
            new QueueLoader(QUEUE, MAXELEMENT, threadStartLatch, QueueLoaderLatch).start();
        }    
        // ждем пока очередь полностью не заполниться
        threadStartLatch.await(); 
        // запускаем менеджер обработки очереди        
        Manager manager = new Manager(GROUPSQUANTITY, PROCESSQUANTITY, QUEUE);
        manager.queueProcessing();
        manager.ElementProcessing();        
    }
}


