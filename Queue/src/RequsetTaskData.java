import java.io.Serializable;

public class RequsetTaskData implements Serializable, ITaskData {
    public RequsetTaskData(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public int a;
    public int b;
    public String id;
}
