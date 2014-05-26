import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class RpcClient {
    private Random random = new Random();
    private ISolver solver;

    public RpcClient(int port) throws MalformedURLException {
        solver = CreateXmlRpcSolver(port);
    }

    public void makeRequests(int count)
    {
        int num = 0;
        do {
            ++num;
            makeRequest();
        }while (num != count && Sleep());
    }

    public void makeRequest() {
        try {
            double a = random.nextInt(100);
            double b = random.nextInt(100);
            double c = random.nextInt(100);

            Solution result = solver.solve(a, b, c);

            if (!result.isExist()) {
                System.out.println("Решение не существует.");
            } else {
                System.out.println(String.format("Корни: %s, %s.", result.getX1(), result.getX2()));
            }
        } catch (Exception e) {
            System.err.println("Message: " + e.getMessage());
            System.err.println("------>");
            e.printStackTrace();
        }
    }

    private static ISolver CreateXmlRpcSolver(int port) throws MalformedURLException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://127.0.0.1:" + Integer.toString(port) + "/solve"));
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        ClientFactory factory = new ClientFactory(client);
        return (ISolver) factory.newInstance(ISolver.class);
    }

    private boolean Sleep()
    {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }
}
