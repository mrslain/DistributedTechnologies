import java.io.Serializable;
import java.util.UUID;

public class RequsetTaskData implements Serializable, ITaskData {
    public RequsetTaskData(int a, int b) {
        id = UUID.randomUUID().toString();
        this.a = a;
        this.b = b;
    }

    public int a;
    public int b;
    public String id;
}
