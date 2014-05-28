import org.zeromq.ZMQ;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {
    public void run() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            ConcurrentSkipListMap<String, RequestTaskData> requestsCash = new ConcurrentSkipListMap<String, RequestTaskData>();
            ConcurrentLinkedQueue<RequestTaskData> requestsQueue = new ConcurrentLinkedQueue<RequestTaskData>();
            ConcurrentLinkedQueue<ResponseTaskData> responsesQueue = new ConcurrentLinkedQueue<ResponseTaskData>();

            executorService.execute(new MasterSender(requestsQueue, requestsCash));
            executorService.execute(new MasterRecipient(responsesQueue));
            executorService.execute(new Reporter(requestsCash, responsesQueue));
            executorService.execute(new RequestGenerator(requestsQueue));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private class RequestGenerator implements Runnable {
        private ConcurrentLinkedQueue<RequestTaskData> requestsQueue;
        private Random random = new Random();

        private RequestGenerator(ConcurrentLinkedQueue<RequestTaskData> requestsQueue) {
            this.requestsQueue = requestsQueue;
        }

        public void run() {
            while (true) {
                requestsQueue.add(new RequestTaskData(random.nextInt(100), random.nextInt(100)));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MasterSender implements Runnable {
        private ConcurrentLinkedQueue<RequestTaskData> requestsQueue;
        private ConcurrentSkipListMap<String, RequestTaskData> requestsCash;

        public MasterSender(ConcurrentLinkedQueue<RequestTaskData> requestsQueue, ConcurrentSkipListMap<String, RequestTaskData> requestsCash) {
            this.requestsQueue = requestsQueue;
            this.requestsCash = requestsCash;
        }

        public void run() {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket publisher = context.socket(ZMQ.PUB);
            publisher.bind("tcp://*:3147");
            try {
                while (true) {
                    RequestTaskData request = requestsQueue.poll();
                    if (request != null) {
                        publisher.send(Serializer.serialize(request), 0);
                        requestsCash.putIfAbsent(request.id, request);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            publisher.close();
            publisher.close();
            context.term();
        }
    }

    private class MasterRecipient implements Runnable {
        private ConcurrentLinkedQueue<ResponseTaskData> responsesQueue;

        public MasterRecipient(ConcurrentLinkedQueue<ResponseTaskData> responsesQueue) {
            this.responsesQueue = responsesQueue;
        }

        public void run() {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket receiver = context.socket(ZMQ.PULL);
            receiver.bind("tcp://*:3146");

            try {
                while (true) {
                    ResponseTaskData response = Serializer.deserialize(receiver.recv(0));
                    responsesQueue.add(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            receiver.close();
            receiver.close();
            context.term();
        }
    }

    private class Reporter implements Runnable {
        private ConcurrentSkipListMap<String, RequestTaskData> requestsCash;
        private ConcurrentLinkedQueue<ResponseTaskData> responsesQueue;

        public Reporter(ConcurrentSkipListMap<String, RequestTaskData> requestsCash, ConcurrentLinkedQueue<ResponseTaskData> responsesQueue) {

            this.requestsCash = requestsCash;
            this.responsesQueue = responsesQueue;
        }

        public void run() {
            while (true) {
                ResponseTaskData responseTaskData = responsesQueue.poll();
                if (responseTaskData != null) {
                    RequestTaskData requsetTaskData = requestsCash.get(responseTaskData.id);
                    System.out.println(String.format("%s + %s = %s", requsetTaskData.a, requsetTaskData.b, responseTaskData.answer));
                }
            }
        }
    }
}
