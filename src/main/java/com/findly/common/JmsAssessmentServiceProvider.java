package com.findly.common;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import com.findly.common.assessment.AssessmentServiceProvider;
import com.findly.common.jms.JmsHelper;

public class JmsAssessmentServiceProvider implements AssessmentServiceProvider {

	public static void main(String[] args) throws Exception {
		// get the initial context
		// InitialContext ctx = new InitialContext();

		// lookup the queue object
		// Queue queue = (Queue) ctx.lookup("queue/testQueue");

		// lookup the queue connection factory
		// QueueConnectionFactory connFactory = (QueueConnectionFactory)
		// ctx.lookup("queue/connectionFactory");

		// create a queue connection
		// QueueConnection queueConn =
		// null;//connFactory.createQueueConnection();
		//
		// queueConn.start();
		//
		// // create a queue session
		// QueueSession queueSession = queueConn.createQueueSession(false,
		// Session.DUPS_OK_ACKNOWLEDGE);
		//
		// // create a queue sender
		// QueueRequestor requestor = new QueueRequestor(queueSession, queue);
		//
		// // create a simple message
		// MapMessage message = queueSession.createMapMessage();
		//
		// // send the messages
		// int numMsgs = 1000;
		// for (int i = 0; i < numMsgs; i++) {
		// int n = (int) (1 + 10 * Math.random());
		// for (int j = 0; j < n; j++)
		// message.setInt("item"+j, (int) (50 * Math.random()));
		// TextMessage result = (TextMessage) requestor.request(message);
		// message.clearBody();
		// System.out.println(result.getText());
		// }
		//
		// // close the queue connection
		// queueConn.close();
	}

	@Override
	public Response<List<String>> startAssessment(String candidateId)
			throws Exception {

		QueueConnection queueConn = JmsHelper.INSATNCE.createQueueConnection();
		Queue queue = JmsHelper.INSATNCE.getQueue("queue/testQueue");
		// create a queue session
		QueueSession queueSession = queueConn.createQueueSession(false,
				Session.DUPS_OK_ACKNOWLEDGE);

		queueConn.start();
		// create a queue sender
		QueueRequestor requestor = new QueueRequestor(queueSession, queue);


		// create a simple message
		TextMessage message = queueSession.createTextMessage(candidateId);

		TextMessage result = (TextMessage) requestor.request(message);

		System.out.println(result.getText());

		// close the queue connection
		queueConn.close();
		List<String> payLoad = new ArrayList<String>();
		payLoad.add(result.toString());
		Response<List<String>> response = new Response<List<String>>();
		response.setData(payLoad);
		return response;
	}
}