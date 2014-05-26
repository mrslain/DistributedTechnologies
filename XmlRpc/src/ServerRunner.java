import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerRunner
{
    public void run(int port)
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new RpcServer(port));
    }
}
