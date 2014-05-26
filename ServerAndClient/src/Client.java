import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Random;

public class Client {
    public Client(String name, int port)
    {
        this.name = name;
        this.port = port;
    }

    public void makeRequests(int count)
    {
        int num = 0;

        try {
            do {
                ++num;
                makeRequest();

            }
            while (num != count && Sleep());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void makeRequest()
    {
        int a = random.nextInt();
        int b = random.nextInt();

        DataInputStream input = null;
        DataOutputStream output = null;
        Socket server = null;

        try {
            try {
                server = new Socket(name, port);

                input = new DataInputStream(server.getInputStream());
                output = new DataOutputStream(server.getOutputStream());

                output.writeChars(Integer.toString(a) + " " + Integer.toString(b));
                output.flush();

                int result = input.readInt();

                output.close();
                input.close();
                server.close();

                System.out.println(Integer.toString(a) + " + " + Integer.toString(b) + " = " + Integer.toString(result));

            } catch (Exception e) {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
                e.printStackTrace();
            } finally {
                if(server != null)
                    server.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean Sleep() throws InterruptedException {
        Thread.sleep(3000);
        return true;
    }

    private Random random = new Random();
    private String name;
    private int port;
}
