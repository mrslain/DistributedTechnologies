import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable
{
    public Server(int port)
    {
        this.port = port;
    }

    public void run()
    {
        ServerSocket ss = null;

        try
        {
            ss = new ServerSocket(port);

            while(true) {
                Socket client;
                try {
                    client = ss.accept();
                }
                catch (Exception e) {
                    break;
                }
                if(client == null || client.isClosed())
                    break;

                DataInputStream input = null;
                DataOutputStream output = null;

                try
                {
                    input = new DataInputStream(client.getInputStream());
                    output = new DataOutputStream(client.getOutputStream());
                    output.writeInt(input.readInt() + input.readInt());
                    output.flush();
                }
                catch(Exception e)
                {
                    if(input != null)
                        input.close();
                    if(output != null)
                        output.close();
                    e.printStackTrace();
                }

                client.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                if (ss != null)
                    ss.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int port;
}
