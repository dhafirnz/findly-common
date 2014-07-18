package com.findly.common;


import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.findly.common.assessment.AssessmentServiceProvider;
import com.findly.common.jms.JmsHelper;

public class JmsAssessmentServiceProvider implements AssessmentServiceProvider {
	
	
	@Override
	public Response<List<String>> startAssessment(String candidateId)
			throws Exception {

		Connection queueConn = JmsHelper.INSATNCE.createConnection();
		Queue queue = JmsHelper.INSATNCE.getQueue("queue/testQueue");
		// create a queue session
		Session session = queueConn.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);

		queueConn.start();
		// create a queue sender
		//Creates a MessageProducer and a TextMessage:

		MessageProducer producer = session.createProducer(queue);
		TextMessage message = session.createTextMessage();

		message.setText(candidateId);
		producer.send(message);
//		, new CompletionListener(){
//
//			@Override
//			public void onCompletion(Message message) {
//				// TODO Auto-generated method stub
//				log(message.toString());
//			}
//
//			@Override
//			public void onException(Message message, Exception exception) {
//				// TODO Auto-generated method stub
//				log(exception.getMessage()+", ERROR:"+message.toString());
//			}
//			
//		});
		

		// close the queue connection
		//JmsHelper.closeResources(queueConn);
		queueConn.close();
		List<String> payLoad = new ArrayList<String>();
		payLoad.add("Message Sent");
		Response<List<String>> response = new Response<List<String>>();
		response.setData(payLoad);
		return response;
	}
	
	
	private void log(String msg){
		System.out.println(msg);
	}
}