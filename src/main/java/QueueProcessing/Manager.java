package QueueProcessing;

import Element.Element;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager {

    BlockingQueue<Element> COMMONQUEUE;
    static Map<Integer, BlockingQueue<Element>> sortedMap;
    BlockingQueue<Element> separatedQueue;
    QueueProcessor processor;
    private int capacity;
    final int GROUPSQUANTITY;
    final int PROCESSQUANTITY;

    private static CountDownLatch latch;
    // Компаратор для приоритетной очереди
    Comparator<Element> comparator = (Element o1, Element o2) -> {
        if (o1.getItemId() < o2.getItemId()) {
            return -1;
        } else if (o1.getItemId() > o2.getItemId()) {
            return 1;
        }
        return 0;
    };

    public Manager(int groupsQuantity, int processQuantity, BlockingQueue COMMONQUEUE) {
        GROUPSQUANTITY = groupsQuantity;
        PROCESSQUANTITY = processQuantity;
        this.COMMONQUEUE = COMMONQUEUE;
        capacity = this.COMMONQUEUE.size();
        sortedMap = new TreeMap<>();
        latch = new CountDownLatch(PROCESSQUANTITY);
        for (int id = 0; id < GROUPSQUANTITY; id++) {
            separatedQueue = new PriorityBlockingQueue<>(capacity, comparator);
            sortedMap.put(id, separatedQueue);
        }
    }
 // Сортировка очереди на группы
    public void queueProcessing() {
        Element element;
        while (!COMMONQUEUE.isEmpty()) {
            try {
                element = COMMONQUEUE.take();
                sortedMap.get(element.getGroupId()).put(element);
            } catch (InterruptedException ex) {
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //определяем приоритет для группы
    // на случай неоднородных данных в очереди
    public int[] getPriority() {
        int[] priority = new int[sortedMap.size()];
        for (int i = 0; i < GROUPSQUANTITY; i++) {
            priority[i] = sortedMap.get(i).size();
        }
        int max = 0;
        for (int i : priority) {
            if (i > max) {
                max = i;
            }
        }
        for (int i = 0; i < GROUPSQUANTITY; i++) {
            priority[i] = ((priority[i] * 10) / max);
            if (priority[i] == 0) {
                priority[i]++;
            }
        }
        return priority;
    }
    
    public void ElementProcessing() {
        int[] priority = getPriority();
        int k = 0; 
        ArrayList<BlockingQueue<Element>> queuesList;
        
        int freeProcess = PROCESSQUANTITY;
        int freeGroups = GROUPSQUANTITY;

        for (int i = 0; i < PROCESSQUANTITY; i++) {
          int threadPriority =0; 
            queuesList = new ArrayList<BlockingQueue<Element>>();
            int limit = (freeGroups / freeProcess); // количество групп отданных процессу
            int count = 0; //счетчик для равномерного распределения групп по потокам
            for (int j = k; j < GROUPSQUANTITY; j++) {
                if (limit > count) {
                    queuesList.add(sortedMap.get(j));
                    threadPriority = Math.max(threadPriority, priority[j]);
                    freeGroups--;
                    count++;
                } else {
                    k = j ;
                    count = 0;
                    freeProcess--;
                    break;
                }
            }
            new QueueProcessor(queuesList, latch, threadPriority ).start();
        }
    }

}
