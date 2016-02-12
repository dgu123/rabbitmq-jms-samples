package io.pivotal.pa.rabbitmq.jms.raw.client;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.pivotal.pa.rabbitmq.jms.raw.config.AppProperties;

public class SimpleMessageListener implements MessageListener {

	private static Logger log = LoggerFactory.getLogger(SimpleMessageListener.class);
	
	@Autowired
	private Session session;
	
	@Autowired
	private AppProperties appProperties;
	
	@Autowired(required=false)
	private MessageProducer backoutQueueProducer;
	
	@Autowired(required=false)
	private MessageProducer reQueueProducer;
	
	private Map<Destination, MessageProducer> messageProducers = new HashMap<>();
	
	private long messageCounter = 0;
	
	@Override
	public void onMessage(Message message) {
		String payload = null;

		//Get the payload
		try {
			if (message instanceof TextMessage) {
				payload = ((TextMessage) message).getText();
			} 
			else if(message instanceof BytesMessage) {
				BytesMessage bMessage = (BytesMessage) message;
				int payloadLength = (int)bMessage.getBodyLength();
				byte payloadBytes[] = new byte[payloadLength];
				bMessage.readBytes(payloadBytes);
				payload = new String(payloadBytes);
			}
			else {
				log.warn("Message not recognized as a TextMessage or BytesMessage.  It is of type: "+message.getClass().toString());
				payload = message.toString();
			}
			
			//poisonTryLimit is our flag to look for poison.
			if(appProperties.poisonTryLimit > 0 && payload.endsWith(appProperties.poisonMessage)) {
				log.info("Received a poison message.");
				handlePoison(message);
			}
			else {
				replyToMessage(message.getJMSReplyTo(), message.getJMSMessageID(), payload);
			}			
			
		} catch (JMSException e) {
			e.printStackTrace();
		}

		String outputText = LocalTime.now().toString();
		if(appProperties.showCounter) { outputText += "["+messageCounter+"]"; }
		outputText += "> "+payload;
		System.out.println(outputText);
	}
	
	private void handlePoison(Message message) {
		try {
			int messageTryCount = 0;
			if(message.propertyExists("MessageTryCount")) {
				String messageTryCountStr = message.getStringProperty("MessageTryCount");
				messageTryCount = Integer.parseInt(messageTryCountStr);
			}
			messageTryCount++;
			if(messageTryCount < appProperties.poisonTryLimit) {
				log.info("Try limit is "+appProperties.poisonTryLimit+" and messageTryCount is "+messageTryCount+", requeueing.");
				message.clearProperties();
				message.setStringProperty("MessageTryCount", ""+messageTryCount);
				reQueueProducer.send(message);
			}
			else {
				log.info("Sending to backout queue");
				backoutQueueProducer.send(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Look for the replyTo field, if it's there echo the message
	private void replyToMessage(Destination replyTo, String id, String payload) {
		try {
			if(replyTo != null) {
				MessageProducer producer = messageProducers.get(replyTo);
				if(producer == null) {
					log.info("Creating a new MessageProducer to use for replys.  Destination: "+replyTo.toString());
					producer = session.createProducer(replyTo);
					messageProducers.put(replyTo, producer);
				}
                Message requestMessage = session.createTextMessage(payload);
                requestMessage.setJMSCorrelationID(id);
                producer.send(requestMessage);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

}
