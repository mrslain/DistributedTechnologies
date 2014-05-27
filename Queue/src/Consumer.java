import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Consumer<T> {
    private final Channel channel;
    private final String queueName;
    private ConcurrentLinkedQueue<T> queueRuqeusts;

    public Consumer(String hostName, String queueName, ConcurrentLinkedQueue<T> queueRuqeusts) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(hostName);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, true, false, false, null);

        this.channel = channel;
        this.queueName = queueName;
        this.queueRuqeusts = queueRuqeusts;
    }

    public void StartRecive() throws IOException, InterruptedException, ClassNotFoundException {
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, false, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            T obj = Serializer.deserialize(delivery.getBody());
            queueRuqeusts.add(obj);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }
}
