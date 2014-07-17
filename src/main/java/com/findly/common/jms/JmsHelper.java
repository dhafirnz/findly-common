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

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
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
	
	INSATNCE;

	//protected static final Logger log = LoggerFactory.getLogger(JmsHelper.class);
	
	private InitialContext ctx;
	private QueueConnectionFactory connectionFactory;

	private JmsHelper() {
		initContext();                
	}
	
	
	
	public QueueConnection createQueueConnection() throws JMSException{
		return connectionFactory.createQueueConnection();
	}
	
	public Queue getQueue(String queueName) throws JMSException, NamingException{
		//  Perform a lookup on the queue
        Queue queue = (Queue)ctx.lookup(queueName);
        return queue;
	}
	
	private void initContext()  {
	
		Properties props = new Properties();
//		props.put(Context.INITIAL_CONTEXT_FACTORY,	"org.jnp.interfaces.NamingContextFactory");
//		props.put(Context.URL_PKG_PREFIXES," org.jboss.naming:org.jnp.interfaces");
//		props.put(Context.PROVIDER_URL, "jnp://localhost:1099");
		try {
//			log.info("Creating InitialContext.");
			ctx =  new InitialContext();
	//		log.info("InitialContext created.");
		//	log.info("Creating Queue Connection Factory.");
			 Object conn = ctx.lookup("/ConnectionFactory");
			 connectionFactory = (QueueConnectionFactory)conn; 
			//log.info("Queue Connection Factory created.");
		} catch (NamingException e) {
			//log.error("Failed to create an InitialContext  ", e);
			e.printStackTrace();
		}				
	}


	/**
	 * Closes any number of JMS closeable resources.
	 * @param resources
	 * @throws Exception
	 */
	public static final void closeResources(final AutoCloseable ... resources) throws Exception {
		for(AutoCloseable resource: resources){
			if (resource != null) {
				resource.close();
			}
		}
	}

	public static final void main(String[] args)  {
		try {
			QueueConnection conn = JmsHelper.INSATNCE.createQueueConnection();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
