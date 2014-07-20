/**
 * 
 */
package com.findly.common;

/**
 * @author Dhafir Moussa
 *
 */
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.findly.common.jms.JmsConstants;
import com.findly.common.jms.JmsHelper;

public class JmsAssessmentConsumer implements MessageListener, Runnable, ExceptionListener {
	private static final Logger log = LoggerFactory.getLogger(JmsAssessmentConsumer.class);
	private QueueConnection connection;
	public JmsAssessmentConsumer() {

		new Thread(this).start();
	}

	@Override
	public void run() {
		connection = JmsHelper.INSATNCE.createMessageConsumer(JmsConstants.ASSESSMENT_QUEUE, this, this);

	}

	/**
	 * This method is called asynchronously by JMS when a message arrives at the
	 * queue. Client applications must not throw any exceptions in the onMessage
	 * method.
	 * 
	 * @param message A JMS message.
	 */

	@Override
	public void onMessage(Message message) {
		ObjectMessage txtMsg = (ObjectMessage) message;
		String result = "We received " + txtMsg;
		log(result);
	}

	/**
	 * This method is called asynchronously by JMS when some error occurs. When
	 * using an asynchronous message listener it is recommended to use an
	 * exception listener also since JMS have no way to report errors otherwise.
	 * 
	 * @param exception A JMS exception.
	 */
	@Override
	public void onException(JMSException exception) {
		System.err.println("something bad happended: " + exception);
	}

	private void log(String msg) {
		log.info(msg);
	}
}