/**
 * 
 */
package com.findly.common;

/**
 * @author Dhafir Moussa
 *
 */
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.findly.common.jms.JmsHelper;

public class JmsAssessmentConsumer implements MessageListener, Runnable,
		ExceptionListener {
	private static final Logger log = LoggerFactory
			.getLogger(JmsAssessmentConsumer.class);
	private Session _session;

	public final static void main(String[] args) {
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

		new Thread(this).start();
	}

	private void init() {
		try {
			// get the initial context
			Connection queueConn = JmsHelper.INSATNCE.createConnection();
			Queue queue = JmsHelper.INSATNCE.getQueue("queue/testQueue");
			// create a queue session
			_session = queueConn.createSession(false,
					Session.DUPS_OK_ACKNOWLEDGE);

			queueConn.start();

			// create a queue receiver
			MessageConsumer queueReceiver = _session.createConsumer(queue);

			// set an asynchronous message listener
			queueReceiver.setMessageListener(this);

			// set an asynchronous exception listener on the connection
			queueConn.setExceptionListener(this);

			// start the connection
			queueConn.start();

			System.out.println("server listening on " + queue);
		} catch (Exception e) {
			log(e.getMessage());
		}
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
			if (reply != null) {
				MessageProducer sender = _session.createProducer(reply);
				sender.send(tm);
			}
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

	@Override
	public void run() {
		init();
	}

	private void log(String msg) {
		log.info(msg);
	}
}