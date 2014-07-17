/**
 * 
 */
package com.findly.common;

/**
 * @author Dhafir Moussa
 *
 */
import java.util.Enumeration;

import javax.naming.InitialContext;

import javax.naming.NamingException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Message;
import javax.jms.MapMessage;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import javax.jms.JMSException;
import javax.jms.ExceptionListener;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.QueueReceiver;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

import com.findly.common.jms.JmsHelper;

public class JmsAssessmentConsumer implements MessageListener,
		ExceptionListener {
	private QueueSession _session;

	public final static void main(String[] args){
		try {
			new JmsAssessmentConsumer();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public JmsAssessmentConsumer() throws JMSException, NamingException {

		waitForever();
	}

	private void init() throws JMSException, NamingException {
		// get the initial context
		QueueConnection queueConn = JmsHelper.INSATNCE.createQueueConnection();
		Queue queue = JmsHelper.INSATNCE.getQueue("queue/testQueue");
		// create a queue session
		_session = queueConn.createQueueSession(false,
				Session.DUPS_OK_ACKNOWLEDGE);

		queueConn.start();

		// create a queue receiver
		QueueReceiver queueReceiver = _session.createReceiver(queue);

		// set an asynchronous message listener
		queueReceiver.setMessageListener(this);

		// set an asynchronous exception listener on the connection
		queueConn.setExceptionListener(this);

		// start the connection
		queueConn.start();

		System.out.println("server listening on " + queue);
	}

	/**
	 * This method is called asynchronously by JMS when a message arrives at the
	 * queue. Client applications must not throw any exceptions in the onMessage
	 * method.
	 * 
	 * @param message
	 *            A JMS message.
	 */
	public void onMessage(Message message) {
		try {

			TextMessage txtMsg = (TextMessage) message;
			String result = "We received " + txtMsg;
			TextMessage tm = _session.createTextMessage(result);
			Queue reply = (Queue) message.getJMSReplyTo();
			QueueSender sender = _session.createSender(reply);
			sender.send(tm);
		} catch (JMSException ex) {
			onException(ex);
		}
	}

	/**
	 * This method is called asynchronously by JMS when some error occurs. When
	 * using an asynchronous message listener it is recommended to use an
	 * exception listener also since JMS have no way to report errors otherwise.
	 * 
	 * @param exception
	 *            A JMS exception.
	 */
	public void onException(JMSException exception) {
		System.err.println("something bad happended: " + exception);
	}

	// Handy utility to signal that I'm done
	synchronized void waitForever() throws JMSException, NamingException {
		init();
		while (true) {
			try {
				wait();
			} catch (InterruptedException ex) {
			}
		}
	}
}