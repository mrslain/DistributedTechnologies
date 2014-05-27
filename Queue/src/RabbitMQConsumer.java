import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class RabbitMQConsumer {
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
        String queueName = "myQueue";
        String routingKey = "testRoute";
        boolean durable = true;
        channel.exchangeDeclare(exchangeName, "direct", durable);
        channel.queueDeclare(queueName, durable,false,false,null);
        channel.queueBind(queueName, exchangeName, routingKey);
        boolean noAck = false;
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, noAck, consumer);
        boolean runInfinite = true;
        while (runInfinite) {
            QueueingConsumer.Delivery delivery;
            try {
                delivery = consumer.nextDelivery();
            } catch (InterruptedException ie) {
                continue;
            }

            TaskData taskData = deserialize(delivery.getBody());
            if(taskData == null)
                throw new Exception("Что-то пошло не так! Объект недесериализовался.");


            System.out.println("Получено сообщение: "
                    + taskData.message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
        channel.close();
        conn.close();
    }

    private static <T> T deserialize(byte[] bytes)
    {
        try {
            ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (T) oin.readObject();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}