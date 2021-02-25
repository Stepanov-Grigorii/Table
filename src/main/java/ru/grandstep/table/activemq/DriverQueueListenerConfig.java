package ru.grandstep.table.activemq;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

@Singleton
@Startup
public class DriverQueueListenerConfig {
    @Inject
    DriverQueueListener driverQueueListener;
    @PostConstruct
    public void init() throws JMSException, NamingException {
        Properties props = new Properties();
        props.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.put("java.naming.provider.url", "tcp://localhost:61616");
        props.put("queue.js-queue", "new-drivers");
        props.put("connectionFactoryNames", "queueCF");

        Context context = new InitialContext(props);

        QueueConnectionFactory connectionFactory = (QueueConnectionFactory) context.lookup("queueCF");
        Queue queue = (Queue) context.lookup("js-queue");

        QueueConnection connection = connectionFactory.createQueueConnection();
        connection.start();

        QueueSession session = connection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

        QueueReceiver receiver = session.createReceiver(queue);

        receiver.setMessageListener(driverQueueListener);
    }
}
