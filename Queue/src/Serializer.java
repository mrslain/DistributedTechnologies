import java.io.*;

public class Serializer {
    public static <T> byte[] serialize(T object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream);

        stream.writeObject(object);
        stream.flush();

        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return (T) oin.readObject();
    }
}
