import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class Produser {
    private final String queueName;
    private Channel channel;

    public Produser(String hostname, String queueName) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostname);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        this.channel = channel;
        this.queueName = queueName;
    }

    public void Queue(ITaskData taskData) throws IOException {
        channel.basicPublish("", queueName, null, Serializer.serialize(taskData));
    }
}

