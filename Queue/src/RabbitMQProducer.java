import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class RabbitMQProducer {
    public static void main(String []args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();
        String exchangeName = "myExchange";
        String routingKey = "testRoute";

        TaskData taskData = new TaskData();
        taskData.message = "Hello, world №2!";


        channel.basicPublish(exchangeName, routingKey
                ,MessageProperties.PERSISTENT_TEXT_PLAIN, serialize(taskData));
        channel.close();
        conn.close();
    }

    private static byte[] serialize(TaskData taskData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream);

        stream.writeObject(taskData);
        stream.flush();

        return byteArrayOutputStream.toByteArray();
    }
}