package ru.grandstep.Table.beans;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.*;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destination",
                propertyValue = "java:/jms/queue/test-queue"),
        @ActivationConfigProperty(propertyName = "destinationType",
                propertyValue = "javax.jms.Queue")
})
public class MyMessageListener implements MessageListener {
//    private final QueueSession session;
//
//    public MyMessageListener(QueueSession session) {
//        this.session = session;
//    }

    @Override
    public void onMessage(Message message) {
        System.out.println("Received");
//        try {
//            session.commit();
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
    }
}
