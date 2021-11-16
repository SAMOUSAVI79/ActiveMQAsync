import com.google.common.collect.ForwardingQueue;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.Queue;
import java.util.Properties;
//import java.util.Queue;

public class MessageSender {
    public static void main(String[] args) throws NamingException, JMSException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        env.put(Context.PROVIDER_URL,
                "tcp://localhost:61616");
        env.put("queue.queueSampleQueue", "FirstQueue");

        //1) Create and start connection
        InitialContext ctx = new InitialContext(env);

        Queue queue = (Queue) ctx.lookup("queueSampleQueue");
        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
        QueueConnection queueConnection = connectionFactory.createQueueConnection();

        //2) create Queue session
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);

        QueueSender queueSender =  queueSession.createSender(queue);

        //4)create QueueSender object
        queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        //5) create TextMessage object
        //6) write message

        TextMessage message = queueSession.createTextMessage("this is difficult");
        queueSender.send(message);

        System.out.println("sent= " + message.getText());

        queueConnection.close();
    }
}
