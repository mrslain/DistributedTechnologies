import java.io.Serializable;

public class ResponseTaskData implements Serializable, ITaskData {
    public int answer;
    public String id;
    public ResponseTaskData(String id, int answer) {
        this.id = id;
        this.answer = answer;
    }
}
