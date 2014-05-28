import org.zeromq.ZMQ;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Worker {
    public void run()
    {
        try
        {
            ExecutorService executorService = Executors.newFixedThreadPool(3);

            ConcurrentLinkedQueue<RequestTaskData> requestsQueue = new ConcurrentLinkedQueue<RequestTaskData>();
            ConcurrentLinkedQueue<ResponseTaskData> responsesQueue = new ConcurrentLinkedQueue<ResponseTaskData>();

            executorService.execute(new WorkerRecipient(requestsQueue));
            executorService.execute(new WorkerSender(responsesQueue));
            executorService.execute(new WorkerExecutor(requestsQueue, responsesQueue));

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class WorkerRecipient implements Runnable
    {
        private ConcurrentLinkedQueue<RequestTaskData> requestsQueue;

        private WorkerRecipient(ConcurrentLinkedQueue<RequestTaskData> requestsQueue) {

            this.requestsQueue = requestsQueue;
        }

        public void run()
        {
            ZMQ.Context context = ZMQ.context(1);

            ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
            subscriber.connect("tcp://localhost:3147");
            subscriber.subscribe("".getBytes());

            try
            {
                while (true)
                {
                    requestsQueue.add(Serializer.<RequestTaskData>deserialize(subscriber.recv(0)));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            subscriber.close();
            context.term();
        }
    }

    private class WorkerSender implements Runnable
    {
        private ConcurrentLinkedQueue<ResponseTaskData> responsesQueue;

        private WorkerSender(ConcurrentLinkedQueue<ResponseTaskData> responsesQueue) {
            this.responsesQueue = responsesQueue;
        }

        public void run()
        {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket sender = context.socket(ZMQ.PUSH);
            sender.connect("tcp://*:3146");

            try
            {
                while (true)
                {
                    ResponseTaskData response = responsesQueue.poll();
                    if(response != null) {
                        sender.send(Serializer.serialize(response), 0);
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            context.term();
        }
    }

    private class WorkerExecutor implements Runnable {
        private ConcurrentLinkedQueue<RequestTaskData> requestsQueue;
        private ConcurrentLinkedQueue<ResponseTaskData> responseQueue;

        public WorkerExecutor(ConcurrentLinkedQueue<RequestTaskData> requestsQueue, ConcurrentLinkedQueue<ResponseTaskData> responseQueue) {
            this.requestsQueue = requestsQueue;
            this.responseQueue = responseQueue;
        }

        public void run() {
            while (true) {
                RequestTaskData request = requestsQueue.poll();
                if (request != null) {
                    responseQueue.add(new ResponseTaskData(request.id, request.a + request.b));
                }
            }
        }
    }
}
