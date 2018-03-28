package QueueProcessing;

import Element.Element;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueLoader extends Thread {

    private Element element;
    private int itemId;
    private int maxElement;
    private static int Id = 0;

    CountDownLatch cdl;
    CountDownLatch QueueLoaderLatch;
    BlockingQueue<Element> queue;

    public QueueLoader(BlockingQueue<Element> queue, int maxElement, CountDownLatch cdl, CountDownLatch QueueLoaderLatch) {
        this.queue = queue;
        this.maxElement = maxElement;
        this.cdl = cdl;
        this.QueueLoaderLatch = QueueLoaderLatch;
    }

    @Override
    public void run() {
        QueueLoading(queue);
    }

    // Заполнение очереди
    private void QueueLoading(BlockingQueue<Element> queue) {
        int groupId = Id++;
        QueueLoaderLatch.countDown();
        try {
            QueueLoaderLatch.await();
            while (queue.size() < maxElement) {
                element = new Element(++itemId, groupId);
                queue.put(element);
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(QueueLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        cdl.countDown();
    }
}
