import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Master {
    public void run() {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);

            ConcurrentSkipListMap<String, RequestTaskData> requestsCash = new ConcurrentSkipListMap<String, RequestTaskData>();
            ConcurrentSkipListMap<String, ResponseTaskData> responsesCash = new ConcurrentSkipListMap<String, ResponseTaskData>();
            ConcurrentLinkedQueue<RequestTaskData> requestsQueue = new ConcurrentLinkedQueue<RequestTaskData>();

            executorService.execute(new MasterSender(requestsQueue, requestsCash));
            executorService.execute(new MasterRecipient(responsesCash));
            executorService.execute(new RequestGenerator(requestsQueue, responsesCash));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private class RequestGenerator implements Runnable {
        private ConcurrentLinkedQueue<RequestTaskData> requestsQueue;
        private ConcurrentSkipListMap<String, ResponseTaskData> responseCash;

        private HttpServer httpServer;


        private RequestGenerator(final ConcurrentLinkedQueue<RequestTaskData> requestsQueue,
                                 final ConcurrentSkipListMap<String, ResponseTaskData> responseCash) throws IOException {
            this.requestsQueue = requestsQueue;
            this.responseCash = responseCash;

            InetSocketAddress address = new InetSocketAddress("192.168.12.99", 8080);
            httpServer = HttpServer.create(address, 0);
            HttpHandler handler = new HttpHandler() {
                public void handle(HttpExchange exchange) throws IOException {
                    String s = exchange.getRequestURI().getQuery();
                    String resp = "";
                    try
                    {
                        String[] parts = s.split(";|=");
                        String aStr = parts[1];
                        String bStr = parts[3];
                        int a = Integer.parseInt(aStr);
                        int b = Integer.parseInt(bStr);
                        RequestTaskData request = new RequestTaskData(a, b);

                        requestsQueue.add(request);

                        int attempts = 0;
                        while (true)
                        {
                            ++attempts;
                            if(attempts ==6) {
                                resp = "calculation timeout";
                                break;
                            }
                            ResponseTaskData response = responseCash.get(request.id);
                            if(response != null)
                            {
                                resp = String.format("%s + %s = %s", request.a, request.b, response.answer);
                                break;
                            }
                            Thread.sleep(200);
                        }
                    } catch (Exception e)
                    {
                        resp = "server error. =(";
                        e.printStackTrace();
                    }

                    byte[] bytes = resp.getBytes();

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK,bytes.length);
                    exchange.getResponseBody().write(bytes);
                    exchange.close();
                }
            };
            httpServer.createContext("/", handler);
            httpServer.setExecutor(Executors.newSingleThreadExecutor());
        }

        public void run() {
            httpServer.start();
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
        private ConcurrentSkipListMap<String, ResponseTaskData> responsesCash;

        public MasterRecipient(ConcurrentSkipListMap<String, ResponseTaskData> responsesCash) {
            this.responsesCash = responsesCash;
        }

        public void run() {
            ZMQ.Context context = ZMQ.context(1);
            ZMQ.Socket receiver = context.socket(ZMQ.PULL);
            receiver.bind("tcp://*:3146");

            try {
                while (true) {
                    ResponseTaskData response = Serializer.deserialize(receiver.recv(0));
                    responsesCash.putIfAbsent(response.id, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            receiver.close();
            receiver.close();
            context.term();
        }
    }
}
