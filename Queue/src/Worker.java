import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker {
    public void run() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            ConcurrentLinkedQueue<RequsetTaskData> requsetTaskDatas = new ConcurrentLinkedQueue<RequsetTaskData>();
            ConcurrentLinkedQueue<ResponseTaskData> responseTaskDatas = new ConcurrentLinkedQueue<ResponseTaskData>();

            executorService.execute(new WorkerRecipient(requsetTaskDatas));
            executorService.execute(new WorkerSender(responseTaskDatas));
            executorService.execute(new WorkerExecutor(requsetTaskDatas, responseTaskDatas));

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private class WorkerSender implements Runnable {
        private final Produser producer;
        private ConcurrentLinkedQueue<ResponseTaskData> responseTaskDatas;

        public WorkerSender(ConcurrentLinkedQueue<ResponseTaskData> responseTaskDatas) throws IOException {
            this.responseTaskDatas = responseTaskDatas;
            this.producer = new Produser("localhost", "ResponseTaskDatas");
        }

        public void run() {
            while (true) {
                ResponseTaskData response = responseTaskDatas.poll();
                if (response != null) {
                    try {
                        producer.Queue(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class WorkerRecipient implements Runnable {
        private final Consumer<RequsetTaskData> consumer;

        public WorkerRecipient(ConcurrentLinkedQueue<RequsetTaskData> requsetTaskDatas) throws IOException {
            this.consumer = new Consumer<RequsetTaskData>("localhost", "RequestTaskDatas", requsetTaskDatas);
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

    private class WorkerExecutor implements Runnable {
        private ConcurrentLinkedQueue<RequsetTaskData> requsetTaskDatas;
        private ConcurrentLinkedQueue<ResponseTaskData> responseTaskDatas;

        public WorkerExecutor(ConcurrentLinkedQueue<RequsetTaskData> requsetTaskDatas, ConcurrentLinkedQueue<ResponseTaskData> responseTaskDatas) {
            this.requsetTaskDatas = requsetTaskDatas;
            this.responseTaskDatas = responseTaskDatas;
        }

        public void run() {
            while (true) {
                RequsetTaskData requset = requsetTaskDatas.poll();
                if (requset != null) {
                    responseTaskDatas.add(new ResponseTaskData(requset.id, requset.a + requset.b));
                }
            }
        }
    }
}
