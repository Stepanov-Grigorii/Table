package ru.grandstep.table.activemq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.grandstep.table.model.WagonIntegrationDTO;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Startup
@Singleton
public class WagonQueueListenerImpl implements WagonQueueListener{
    @Inject
    private BeanManager beanManager;

    public void onMessage(Message message){
        TextMessage m = (TextMessage) message;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            WagonIntegrationDTO wagon = objectMapper.readValue(m.getText(), WagonIntegrationDTO.class);
            System.out.println(wagon);
            beanManager.fireEvent(wagon);
        } catch (JMSException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
