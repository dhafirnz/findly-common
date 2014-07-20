/**
 * 
 */
package com.findly.common.jms;

/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.Serializable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Dhafir Moussa
 * 
 */
public enum JmsHelper {

	/**
	 * Singleton enum style. The JVM will guarantee unique initialization
	 * without the need for double-locked synchronization or static
	 * initialisation.
	 */
	INSATNCE;// - please don't add another enum to this!

	protected static final Logger log = LoggerFactory.getLogger(JmsHelper.class);

	/**
	 * Initial context
	 */
	private InitialContext ctx;

	/**
	 * One connection factory for Queue message types.
	 */
	private QueueConnectionFactory queueConnectionFactory;

	/**
	 * Init the context and factories
	 */
	private JmsHelper() {
		// - send empty properties if using local (in VM) connection factory
		initContext(new Properties());
	}

	/**
	 * Will create a new {@link Connection} instance. The caller is responsible
	 * for closing the connection after finishing with.
	 * 
	 * @return newly created {@link Connection}
	 * @throws JMSException
	 */
	public QueueConnection createQueueConnection() throws JMSException {
		return queueConnectionFactory.createQueueConnection();
	}

	/**
	 * Looks up and returns a Queue in the local context.
	 * 
	 * @param queueJndiName - Queue name to look up.
	 * @return a queue referenced by the passed JNDI name
	 * @throws JMSException
	 * @throws NamingException
	 */
	public Queue getQueue(String queueJndiName) throws JMSException, NamingException {
		// Perform a lookup on the queue
		Queue queue = (Queue) ctx.lookup(queueJndiName);
		return queue;
	}

	/**
	 * Sends a message on a particular queue.
	 * 
	 * @param queueJndiName - the {@link Queue} JNDI name. If Queue is not found
	 *        then it will be created.
	 * @param messageObj the message object to be sent to the Queue. The object
	 *        needs to be {@link Serializable}.
	 * @throws JMSException
	 * @throws NamingException
	 */
	public <T extends Serializable> void send(String queueJndiName, T messageObj) {
		QueueConnection queueConn = null;
		try {
			queueConn = JmsHelper.INSATNCE.createQueueConnection();
			// create a queue session
			QueueSession session = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);

			// - find queue by JNDI name
			Queue queue = JmsHelper.INSATNCE.getQueue(queueJndiName);
			if (queue == null) {
				// - create the Queue
				throw new NamingException("Couldn't find queue.");
			}

			// queueConn.start();
			QueueSender sender = session.createSender(queue);
			ObjectMessage message = session.createObjectMessage(messageObj);
			sender.send(message);

		} catch (JMSException e) {
			logError("Error sending a message to Queue " + queueJndiName, e);
		} catch (NamingException e) {
			logError("Error attempting to find queue " + queueJndiName, e);
		} finally {
			closeConnection(queueConn);
		}
	}

	/**
	 * Will start a session to listen to the designated queue
	 * 
	 * @param queueJndiName - name of the Queue to listen to.
	 * @param msgListener
	 * @param exceptionListener
	 */
	public QueueConnection createMessageConsumer(String queueJndiName, MessageListener msgListener, ExceptionListener exceptionListener) {
		try {
			// get the initial context
			QueueConnection queueConn = JmsHelper.INSATNCE.createQueueConnection();

			// create a queue session
			Session session = queueConn.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);

			// - find queue by JNDI name
			Queue queue = JmsHelper.INSATNCE.getQueue(queueJndiName);
			if (queue == null) {
				// - create the Queue
				throw new NamingException("Couldn't find queue.");
			}

			// create a queue receiver
			MessageConsumer messageConsumer = session.createConsumer(queue);

			// set an asynchronous message listener
			messageConsumer.setMessageListener(msgListener);

			// set an asynchronous exception listener on the connection
			queueConn.setExceptionListener(exceptionListener);

			// start the connection
			queueConn.start();
			logMsg("JmsHelper started listening to Queue " + queueJndiName);

			return queueConn;

		} catch (Exception e) {
			logError("failed to listent to a queue " + queueJndiName, e);
		}
		return null;
	}

	/**
	 * Closes any number of JMS closeable resources.
	 * 
	 * @param resources
	 * @throws Exception
	 */
	public static final void closeResources(final AutoCloseable... resources) throws Exception {
		for (AutoCloseable resource : resources) {
			if (resource != null) {
				resource.close();
			}
		}
	}

	public static final void closeConnection(final Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (JMSException e) {
				// - we don't care
				logMsg("failed to close a JMS Connection");
			}

		}
	}

	/**
	 * one entry point to message logging.
	 * 
	 * @param msg
	 */
	private static void logMsg(String msg) {
		if (log != null) {
			log.info(msg);
		} else {
			System.out.println(msg);
		}
	}

	private static void logError(String msg, Exception e) {
		if (log != null) {
			log.error(msg, e);
		} else {
			e.printStackTrace();
			System.err.println(msg + e.getMessage());
		}
	}

	/**
	 * Initial env testing only. Must have JBoss running to get it to work.
	 * 
	 * @param args
	 */
	public static final void main(String[] args) {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		props.put(Context.URL_PKG_PREFIXES, " org.jboss.naming:org.jnp.interfaces");
		props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		try {
			Connection conn = JmsHelper.INSATNCE.createQueueConnection();
			logMsg(conn.getMetaData().toString());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialise the local context
	 */
	private void initContext(Properties props) {

		try {
			logMsg("Creating InitialContext.");
			ctx = new InitialContext(props);
			logMsg("InitialContext created.");
			logMsg("Creating Queue Connection Factory.");
			Object conn = ctx.lookup(JmsConstants.CONNECTION_FACTORY);
			queueConnectionFactory = (QueueConnectionFactory) conn;
			logMsg("Queue Connection Factory created.");
		} catch (Exception e) {
			logError("Failed to create an InitialContext  ", e);

		}
	}

}
