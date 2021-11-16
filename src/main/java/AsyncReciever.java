import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class AsyncReciever implements MessageListener, ExceptionListener {

static QueueConnection queueConnection = null;
    public static void main(String[] args) throws NamingException, JMSException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,  "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        env.put(Context.PROVIDER_URL,
                "tcp://localhost:61616");
        env.put("queue.SampleQueue", "FirstQueue");

        //1) Create and start connection
        InitialContext ctx = new InitialContext(env);

        Queue queue = (Queue) ctx.lookup("SampleQueue");
        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) ctx.lookup("QueueConnectionFactory");
        queueConnection = connectionFactory.createQueueConnection();

        //2) create Queue session
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        QueueReceiver queueReceiver = queueSession.createReceiver(queue);

        AsyncReciever asyncReciever = new AsyncReciever();
        queueReceiver.setMessageListener(asyncReciever);
        queueConnection.setExceptionListener(asyncReciever);

        queueConnection.start();

    }




    @Override
    public void onMessage(Message message){

        TextMessage msg = (TextMessage) message;
        try{
            if(msg.getText().equals("exit")){
                queueConnection.close();
                System.out.println("this is Async");
            }else{
                System.out.println("recived : "+ msg.getText());

            }
        }catch (JMSException ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void onException(JMSException e) {
        System.err.println("we have an error : " + e);
    }
}
