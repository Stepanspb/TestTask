package QueueProcessing;

import Element.Element;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueProcessor extends Thread {

    private ArrayList<BlockingQueue<Element>> queues;
    private int threadPriority;
    private CountDownLatch latch;


    public QueueProcessor(ArrayList<BlockingQueue<Element>> queues, CountDownLatch latch, int threadPriority) {
        this.queues = queues;
        this.latch = latch;
        this.threadPriority = threadPriority;
    }
    
    public int getCoefficient(){
        int coefficient = Integer.MAX_VALUE ;        
        for (BlockingQueue<Element> queue : queues) {
           coefficient = Math.min(coefficient, queue.size());       
        }     
        return coefficient;
    }

    @Override
    public void run() {
        Element element;
        
        try {
            this.setPriority(threadPriority);
            int coefficient = getCoefficient();
            int[] ratio = new int[queues.size()];
            /* в случае неоднородных данных позволяет обрабатывать группу с бОльшим
            количеством элементов быстрее
            */
            int idx = 0;
            for (BlockingQueue<Element> queue : queues) {
            ratio[idx++] = (queue.size()/coefficient);    
            }
            // ждем потоки
            latch.countDown();
            latch.await();
            int n =0;
            while (!queues.isEmpty()) {
                Thread.sleep(1);
                for (BlockingQueue<Element> queue : queues) {
                    if (queue.isEmpty()) {
                        queues.remove(queue);
                        break;
                    }                    
                    for (int i = 0; i < ratio[n]; i++) {
                        if (!queue.isEmpty()) {
                            element = queue.take();
                            element.elementPrint(element.getItemId(), element.getGroupId());
                        }
                    }
                    n++;
                }
                n=0;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(QueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
    
    

