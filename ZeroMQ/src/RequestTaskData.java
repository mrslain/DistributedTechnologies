import java.util.UUID;

public class RequestTaskData implements ITaskData {
    public int a;
    public int b;
    public String id;

    public RequestTaskData(int a, int b) {
        id = UUID.randomUUID().toString();
        this.a = a;
        this.b = b;
    }
}
