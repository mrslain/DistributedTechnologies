import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {

    public void run() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            ConcurrentSkipListMap<String, RequsetTaskData> tasks = new ConcurrentSkipListMap<String, RequsetTaskData>();
            ConcurrentLinkedQueue<ResponseTaskData> responseTaskDatas = new ConcurrentLinkedQueue<ResponseTaskData>();
            executorService.execute(new MasterSender(tasks));
            executorService.execute(new MasterRecipient(responseTaskDatas));
            executorService.execute(new Reporter(tasks, responseTaskDatas));

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    private class MasterSender implements Runnable {
        private final Produser producer;
        private Random random = new Random();
        private ConcurrentSkipListMap<String, RequsetTaskData> requestTaskDatas;

        public MasterSender(ConcurrentSkipListMap<String, RequsetTaskData> requestTaskDatas) throws IOException {
            this.requestTaskDatas = requestTaskDatas;
            this.producer = new Produser("localhost", "RequestTaskDatas");
        }

        public void run() {
            RequsetTaskData req = new RequsetTaskData(random.nextInt(), random.nextInt());
            requestTaskDatas.put(req.id, req);
            try {
                producer.Queue(req);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MasterRecipient implements Runnable {
        private final Consumer<ResponseTaskData> consumer;

        private MasterRecipient(ConcurrentLinkedQueue<ResponseTaskData> responseQueue) throws IOException {
            this.consumer = new Consumer<ResponseTaskData>("localhost", "ResponseTaskDatas", responseQueue);
        }

        public void run() {
            try {
                consumer.StartRecive();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class Reporter implements Runnable {
        private ConcurrentSkipListMap<String, RequsetTaskData> requestTaskDatas;
        private ConcurrentLinkedQueue<ResponseTaskData> responseQueue;

        public Reporter(ConcurrentSkipListMap<String, RequsetTaskData> requestTaskDatas, ConcurrentLinkedQueue<ResponseTaskData> responseQueue) {
            this.requestTaskDatas = requestTaskDatas;
            this.responseQueue = responseQueue;
        }

        public void run() {
            while (true) {
                ResponseTaskData responseTaskData = responseQueue.poll();
                if (responseTaskData != null) {
                    RequsetTaskData requsetTaskData = requestTaskDatas.get(responseTaskData.id);
                    System.out.println(String.format("%s + %s = %s", requsetTaskData.a, requsetTaskData.b, responseTaskData.answer));
                }
            }
        }
    }
}