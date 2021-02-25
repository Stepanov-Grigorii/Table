package ru.grandstep.table.activemq;

import javax.ejb.Local;
import javax.jms.MessageListener;

@Local
public interface DriverQueueListener extends MessageListener {
}
